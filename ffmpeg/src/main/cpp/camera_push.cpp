//
// Created by 蒋建伟 on 2020-01-01.
//
#include <jni.h>
#include "logutil.h"
#include  <string>
#include <exception>

#ifdef __cplusplus
extern "C" {
#endif
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/imgutils.h"
#include "libavutil/time.h"
#include "libavutil/error.h"
//=======================================================================
//
// 摄像头采集数据并输出（文件/RTMP推流）
//
//=======================================================================


AVFormatContext *ofmt_ctx;
AVStream *video_st;
AVCodecContext *pCodecCtx;
AVCodec *pCodec;
AVPacket enc_pkt;
AVFrame *pFrameYUV;
int count = 0;
int yuv_width;
int yuv_height;
int y_length;
int uv_length;
int width = 480;
//int width = 640;
int height = 320;
//int height = 480;
int fps = 15;

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_initVideo(JNIEnv *env, jobject thiz, jstring url, jint jwidth,
                                             jint jheight) {
//    const char *out_path = env->GetStringUTFChars(url, 0);
////    LOGE(out_path);
//    width = jwidth;
//    height = jheight;
//    //计算yuv数据的长度
//    yuv_width = width;
//    yuv_height = height;
//    y_length = width * height;
//    uv_length = width * height / 4;
//
////    av_register_all();
//
//    //output initialize
//    avformat_alloc_output_context2(&ofmt_ctx, NULL, "flv", out_path);
//    //output encoder initialize
//    pCodec = avcodec_find_encoder(AV_CODEC_ID_H264);
//    if (!pCodec) {
//        LOGE("Can not find encoder!\n");
//        return -1;
//    }
//    pCodecCtx = avcodec_alloc_context3(pCodec);
//    //编码器的ID号，这里为264编码器，可以根据video_st里的codecID 参数赋值
//    pCodecCtx->codec_id = pCodec->id;
//    //像素的格式，也就是说采用什么样的色彩空间来表明一个像素点
//    pCodecCtx->pix_fmt = AV_PIX_FMT_YUV420P;
//    //编码器编码的数据类型
//    pCodecCtx->codec_type = AVMEDIA_TYPE_VIDEO;
//    //编码目标的视频帧大小，以像素为单位
//    pCodecCtx->width = width;
//    pCodecCtx->height = height;
//    pCodecCtx->framerate = (AVRational) {fps, 1};
//    //帧率的基本单位，我们用分数来表示，
//    pCodecCtx->time_base = (AVRational) {1, fps};
//    //目标的码率，即采样的码率；显然，采样码率越大，视频大小越大
//    pCodecCtx->bit_rate = 400000;
//    //固定允许的码率误差，数值越大，视频越小
////    pCodecCtx->bit_rate_tolerance = 4000000;
//    pCodecCtx->gop_size = 50;
//    /* Some formats want stream headers to be separate. */
//    if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
//        pCodecCtx->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
//
//    //H264 codec param
////    pCodecCtx->me_range = 16;
//    //pCodecCtx->max_qdiff = 4;
//    pCodecCtx->qcompress = 0.6;
//    //最大和最小量化系数
//    pCodecCtx->qmin = 10;
//    pCodecCtx->qmax = 51;
//    //Optional Param
//    //两个非B帧之间允许出现多少个B帧数
//    //设置0表示不使用B帧
//    //b 帧越多，图片越小
//    pCodecCtx->max_b_frames = 0;
//    // Set H264 preset and tune
//    AVDictionary *param = 0;
//    //H.264
//    if (pCodecCtx->codec_id == AV_CODEC_ID_H264) {
////        av_dict_set(&param, "preset", "slow", 0);
//        /**
//         * 这个非常重要，如果不设置延时非常的大
//         * ultrafast,superfast, veryfast, faster, fast, medium
//         * slow, slower, veryslow, placebo.　这是x264编码速度的选项
//       */
//        av_dict_set(&param, "preset", "superfast", 0);
//        av_dict_set(&param, "tune", "zerolatency", 0);
//    }
//
//    if (avcodec_open2(pCodecCtx, pCodec, &param) < 0) {
//        LOGE("Failed to open encoder!\n");
//        return -1;
//    }
//
//    //Add a new stream to output,should be called by the user before avformat_write_header() for muxing
//    video_st = avformat_new_stream(ofmt_ctx, pCodec);
//    if (video_st == NULL) {
//        return -1;
//    }
//    video_st->time_base.num = 1;
//    video_st->time_base.den = fps;
////    video_st->codec = pCodecCtx;
//    video_st->codecpar->codec_tag = 0;
//    avcodec_parameters_from_context(video_st->codecpar, pCodecCtx);
//
//    //Open output URL,set before avformat_write_header() for muxing
//    if (avio_open(&ofmt_ctx->pb, out_path, AVIO_FLAG_READ_WRITE) < 0) {
//        LOGE("Failed to open output file!\n");
//        return -1;
//    }
//
//    //Write File Header
//    avformat_write_header(ofmt_ctx, NULL);


    return 0;
}

int avError(int errNum) {
    char buf[1024];
    //获取错误信息
    av_strerror(errNum, buf, sizeof(buf));
    //LOGE(string().append("发生异常：").append(buf).c_str());
    return -1;
}

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_onFrameCallback(JNIEnv *env, jobject thiz,
                                                   jbyteArray buffer_) {
//    jbyte *in = env->GetByteArrayElements(buffer_, NULL);
//
//    int ret = 0;
//
//    pFrameYUV = av_frame_alloc();
//    int picture_size = av_image_get_buffer_size(pCodecCtx->pix_fmt, pCodecCtx->width,
//                                                pCodecCtx->height, 1);
//    uint8_t *buffers = (uint8_t *) av_malloc(picture_size);
//
//
//    //将buffers的地址赋给AVFrame中的图像数据，根据像素格式判断有几个数据指针
//    av_image_fill_arrays(pFrameYUV->data, pFrameYUV->linesize, buffers, pCodecCtx->pix_fmt,
//                         pCodecCtx->width, pCodecCtx->height, 1);
//
//    //安卓摄像头数据为NV21格式，此处将其转换为YUV420P格式
//    ////N21   0~width * height是Y分量，  width*height~ width*height*3/2是VU交替存储
//    //复制Y分量的数据
//    memcpy(pFrameYUV->data[0], in, y_length); //Y
//    pFrameYUV->pts = count;
//    for (int i = 0; i < uv_length; i++) {
//        //将v数据存到第三个平面
//        *(pFrameYUV->data[2] + i) = *(in + y_length + i * 2);
//        //将U数据存到第二个平面
//        *(pFrameYUV->data[1] + i) = *(in + y_length + i * 2 + 1);
//    }
//
//    pFrameYUV->format = AV_PIX_FMT_YUV420P;
//    pFrameYUV->width = yuv_width;
//    pFrameYUV->height = yuv_height;
//
//    //例如对于H.264来说。1个AVPacket的data通常对应一个NAL
//    //初始化AVPacket
//    av_init_packet(&enc_pkt);
////    __android_log_print(ANDROID_LOG_WARN, "eric", "编码前时间:%lld",
////                        (long long) ((av_gettime() - startTime) / 1000));
//    //开始编码YUV数据
//    ret = avcodec_send_frame(pCodecCtx, pFrameYUV);
//    if (ret != 0) {
//        LOGE("avcodec_send_frame error");
//        return -1;
//    }
//    //获取编码后的数据
//    ret = avcodec_receive_packet(pCodecCtx, &enc_pkt);
////    __android_log_print(ANDROID_LOG_WARN, "eric", "编码时间:%lld",
////                        (long long) ((av_gettime() - startTime) / 1000));
//    //是否编码前的YUV数据
//    av_frame_free(&pFrameYUV);
//    if (ret != 0 || enc_pkt.size <= 0) {
//        LOGE("avcodec_receive_packet error");
//        avError(ret);
//
//        return -2;
//    }
//    enc_pkt.stream_index = video_st->index;
//    AVRational time_base = ofmt_ctx->streams[0]->time_base;//{ 1, 1000 };
//    enc_pkt.pts = count * (video_st->time_base.den) / ((video_st->time_base.num) * fps);
//    enc_pkt.dts = enc_pkt.pts;
//    enc_pkt.duration = (video_st->time_base.den) / ((video_st->time_base.num) * fps);
//    __android_log_print(ANDROID_LOG_WARN, "eric",
//                        "index:%d,pts:%lld,dts:%lld,duration:%lld,time_base:%d,%d",
//                        count,
//                        (long long) enc_pkt.pts,
//                        (long long) enc_pkt.dts,
//                        (long long) enc_pkt.duration,
//                        time_base.num, time_base.den);
//    enc_pkt.pos = -1;
////    AVRational time_base_q = {1, AV_TIME_BASE};
////    //计算视频播放时间
////    int64_t pts_time = av_rescale_q(enc_pkt.dts, time_base, time_base_q);
////    //计算实际视频的播放时间
////    if (count == 0) {
////        startTime = av_gettime();
////    }
////    int64_t now_time = av_gettime() - startTime;
////    __android_log_print(ANDROID_LOG_WARN, "eric", "delt time :%lld", (pts_time - now_time));
////    if (pts_time > now_time) {
////        //睡眠一段时间（目的是让当前视频记录的播放时间与实际时间同步）
////        av_usleep((unsigned int) (pts_time - now_time));
////    }
//
//    ret = av_interleaved_write_frame(ofmt_ctx, &enc_pkt);
//    if (ret != 0) {
//        LOGE("av_interleaved_write_frame failed");
//    }
//    count++;
//    env->ReleaseByteArrayElements(buffer_, in, 0);
    return 0;
}
#ifdef __cplusplus
}
#endif