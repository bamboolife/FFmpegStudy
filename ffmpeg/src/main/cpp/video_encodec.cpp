//
// Created by 蒋建伟 on 2019-12-29.
//
#include <jni.h>
#include "logutil.h"

#ifdef __cplusplus
extern "C" {
#endif
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/imgutils.h"
//typedef struct VideoEncodec{
//   AVFormatContext *avFormatContext;
//   AVOutputFormat *avOutputFormat;
//   AVCodecParameters *avCodecParameters;
//   AVStream *video_stream;
//   AVCodec *avCodec;
//   AVPacket avPacket;
//   AVFrame *avFrame;
//   AVCodecContext *avCodecContext;
//   uint8_t *picture_buf= nullptr;
//   int size;
//   int in_w=480,int_h=272;
//   int framenum=50;
//   const char * out_file="encodec.mp4";
//
//}VEContext;

int flush_encoder(AVFormatContext *fmt_ctx, unsigned int stream_index) {
    int ret;
    //int got_frame;
    AVPacket enc_pkt;
    AVCodecParameters *avCodecParameters = fmt_ctx->streams[stream_index]->codecpar;
    AVCodec *avCodec = avcodec_find_encoder(avCodecParameters->codec_id);
    AVCodecContext *avCodecContext;
    avcodec_parameters_to_context(avCodecContext, avCodecParameters);
    if (!(avCodec->capabilities & AV_CODEC_CAP_DELAY))
        return 0;
    while (1) {
        printf("Flushing stream #%u encoder\n", stream_index);
        enc_pkt.data = NULL;
        enc_pkt.size = 0;
        av_init_packet(&enc_pkt);
//        ret = avcodec_encode_video2 (fmt_ctx->streams[stream_index]->codec, &enc_pkt,
//                                     NULL, &got_frame);
       // AVFrame *avFrame=fmt_ctx->streams[stream_index]->;
        avcodec_send_frame(avCodecContext,NULL );
        int ret = avcodec_receive_packet(avCodecContext, &enc_pkt);
        av_frame_free(NULL);
        if (ret < 0)
            break;

//        if (!got_frame) {
//            ret = 0;
//            break;
//        }
        LOGE("success encoder 1 frame");
        // parpare packet for muxing
        enc_pkt.stream_index = stream_index;
        AVCodecParameters *parameters=fmt_ctx->streams[stream_index]->codecpar;
        AVCodecContext *pCodecContext;
        avcodec_parameters_to_context(pCodecContext,parameters);
        av_packet_rescale_ts(&enc_pkt,
                             pCodecContext->time_base,
                             fmt_ctx->streams[stream_index]->time_base);
        ret = av_interleaved_write_frame(fmt_ctx, &enc_pkt);
        if (ret < 0)
            break;
    }
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoEncodec_encodec(JNIEnv *env, jobject thiz, jstring inputurl,
                                           jstring outputurl) {
//    const char *input_path;
//    const char *out_path;
   char input_path[500];
   char out_path[500];
    AVFormatContext *avFormatContext;
    AVOutputFormat *avOutputFormat;
    AVCodecParameters *avCodecParameters;
    AVStream *video_stream;
    AVCodec *avCodec;
    AVPacket avPacket;
    AVFrame *picture;
    AVCodecContext *avCodecContext;
    uint8_t *out_buffer = nullptr;
    int buffer_size;
    int in_w = 480, int_h = 272;
    int framenum = 50;
//    input_path = env->GetStringUTFChars(inputurl, NULL);
//    out_path = env->GetStringUTFChars(outputurl, NULL);
    sprintf(input_path,"%s",env->GetStringUTFChars(inputurl,NULL));
    sprintf(out_path,"%s",env->GetStringUTFChars(outputurl,NULL));
    //打开视频
    FILE *in_file = fopen(input_path, "rb");
    if (!in_file) {
        LOGE("不能打开文件");
        return -1;
    }

    //[1]初始化AVFormatContext结构体,根据文件名获取到合适的封装格式
    avformat_alloc_output_context2(&avFormatContext, NULL, NULL, out_path);
    avOutputFormat = avFormatContext->oformat;
    //[1]

    //[2]打开文件
    if (avio_open(&(avFormatContext)->pb, out_path, AVIO_FLAG_READ_WRITE)) {
        LOGE("打开文件失败");
        return -1;
    }
    //[2]

    //[3]初始化视频码流
    video_stream = avformat_new_stream(avFormatContext, 0);
    if (video_stream == NULL) {
        LOGE("初始化视频码流失败");
        return -1;
    }
    video_stream->time_base.num = 1;
    video_stream->time_base.den = 25;
    //[3]

    //[4]编码器Context设置参数
    avCodecParameters = video_stream->codecpar;
    //初始化AVCodecContext 用avCodecParameters直接转换赋值
    avCodecContext=avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(avCodecContext, avCodecParameters);
    avCodecContext->codec_id = avOutputFormat->video_codec;
    avCodecContext->codec_type = AVMEDIA_TYPE_VIDEO;
    avCodecContext->pix_fmt = AV_PIX_FMT_YUV420P;
    avCodecContext->width = in_w;
    avCodecContext->height = int_h;
    avCodecContext->time_base.num = 1;
    avCodecContext->time_base.den = 25;
    avCodecContext->bit_rate = 400000;
    avCodecContext->gop_size = 12;
    if (avCodecContext->codec_id == AV_CODEC_ID_H264) {
        avCodecContext->qmin = 10;
        avCodecContext->qmax = 51;
        avCodecContext->qcompress = 0.6;
    }
    if (avCodecContext->codec_id == AV_CODEC_ID_MPEG2VIDEO)
        avCodecContext->max_b_frames = 2;
    if (avCodecContext->codec_id == AV_CODEC_ID_MPEG1VIDEO)
        avCodecContext->mb_decision = 2;
    //[4]

    //[5]寻找编码器并且打开编码器
    avCodec = avcodec_find_encoder(avCodecContext->codec_id);
    if (!avCodec) {
        LOGE("找不到编码器");
        return -1;
    }
    if (avcodec_open2(avCodecContext, avCodec, NULL) < 0) {
        LOGE("打开编码器失败");
        return -1;
    }
    //[5]

    //输出格式信息
    av_dump_format(avFormatContext, 0, out_path, 1);

    //初始化帧
    picture = av_frame_alloc();
    picture->width = avCodecContext->width;
    picture->height = avCodecContext->height;
    picture->format = avCodecContext->pix_fmt;
    buffer_size = av_image_get_buffer_size(avCodecContext->pix_fmt, avCodecContext->width,
                                           avCodecContext->height, 1);
    out_buffer = (uint8_t *) av_malloc(buffer_size * sizeof(uint8_t));
    av_image_fill_arrays(picture->data, picture->linesize, out_buffer, avCodecContext->pix_fmt,
                         avCodecContext->width, avCodecContext->height, 1);

    //如果视频出现视频帧率很大，导致视频无法播放 就打开下面的代码
    //AVDictionary *opt=NULL;
    //av_dict_set_int(&opt,"video_track_timescale",25,0);

    //[6] 写头文件
  int hret=  avformat_write_header(avFormatContext, NULL);
  if(hret<0){
      LOGE("shi");

      return -1;
  }
    //[6]

    AVPacket pkt;//创建已编码帧
    int y_size = avCodecContext->width * avCodecContext->height;
    av_new_packet(&pkt, buffer_size * 3);

    //[7]循环编码每一帧
    for (int i = 0; i < framenum; ++i) {
        //读取YUV数据
        if (fread(out_buffer, 1, y_size * 3 / 2, in_file) < 0) {
            LOGE("读取YUV数据失败");
            return -1;
        } else {
            if (feof(in_file)) {
                break;
            }
        }
        picture->data[0] = out_buffer; //亮度Y
        picture->data[1] = out_buffer + y_size; //U
        picture->data[2] = out_buffer + y_size * 5 / 4; //V
        //AVFrame PTS
        picture->pts = i;
        int got_picture = 0;

        //编码
        // int ret = avcodec_encode_video2(avCodecContext,&pkt,picture,&got_picture);
        avcodec_send_frame(avCodecContext, picture);
        int ret = avcodec_receive_packet(avCodecContext, &pkt);
        if (ret == 0) {
            LOGE("编码成功");

            // parpare packet for muxing
            pkt.stream_index = video_stream->index;
            av_packet_rescale_ts(&pkt, avCodecContext->time_base, video_stream->time_base);
            pkt.pos = -1;
            ret = av_interleaved_write_frame(avFormatContext, &pkt);
            av_packet_unref(&pkt);
        } else {
            LOGE("编码失败");
            return -1;
        }

    }
    //[7]

    //[8] Flush_encoder
    int ret = flush_encoder(avFormatContext, 0);
    if (ret < 0) {
        LOGE("flushing encoder failed!");
        goto end;
    }
    //[8]

    //[9]写文件尾
    av_write_trailer(avFormatContext);
    //[9]



    end:
    //释放内存
    if (video_stream) {
        avcodec_close(avCodecContext);
        av_free(&buffer_size);
        av_free(&out_buffer);
    }
    if (avFormatContext) {
        avio_close(avFormatContext->pb);
        avformat_free_context(avFormatContext);
    }
    fclose(in_file);
    return 0;
}

#ifdef __cplusplus
}
#endif
