//
// Created by 蒋建伟 on 2019-12-22.
//
#include <jni.h>
#include "logutil.h"
#ifdef __cplusplus
extern "C" {
#endif
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavfilter/avfilter.h"
#include "libavutil/log.h"
#include "libavutil/imgutils.h"
#include "libavutil/frame.h"
#include "libavutil/time.h"
//Output FFmpeg's av_log()
void custom_log(void *ptr, int level, const char *fmt, va_list vl) {
    FILE *fp = fopen("/storage/emulated/0/av_log.txt", "a+");
    if (fp) {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }
}

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_PushStream_stream(JNIEnv *env, jobject thiz, jstring inputurl,
                                         jstring outputurl) {
    AVOutputFormat *ofmt = NULL;
    AVFormatContext *ifmt_ctx = NULL, *ofmt_ctx = NULL;
    AVPacket pkt;

    int ret, i;
    char input_str[500] = {0};
    char output_str[500] = {0};

    int videoindex = -1;
    int frame_index = 0;

    int64_t start_time;
    sprintf(input_str, "%s", env->GetStringUTFChars(inputurl, NULL));
    sprintf(output_str, "%s", env->GetStringUTFChars(outputurl, NULL));


    //FFmpeg av_log() callback
    av_log_set_callback(custom_log);

    //注册所有组件
    av_register_all();
    //初始化网络
    avformat_network_init();

    //打开输入文件
    if ((ret = avformat_open_input(&ifmt_ctx, input_str, 0, 0)) < 0) {
        LOGE("Could not open input file.");
        goto end;
    }
    //寻找流信息
    if ((ret = avformat_find_stream_info(ifmt_ctx, 0)) < 0) {
        LOGE("Failed to retrieve input stream information");
        goto end;
    }


    for (i = 0; i < ifmt_ctx->nb_streams; i++) {
        //找到视频流
        if (ifmt_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoindex = i;
            break;
        }
    }
    av_dump_format(ifmt_ctx, 0, input_str, 0);
    //分配输出封装格式上下文， rtmp协议支持格式为flv
    avformat_alloc_output_context2(&ofmt_ctx, NULL, "flv", output_str); //RTMP
    //avformat_alloc_output_context2(&ofmt_ctx, NULL, "mpegts", output_str);//UDP

    if (!ofmt_ctx) {
        LOGE("Could not create output context\n");
        ret = AVERROR_UNKNOWN;
        goto end;
    }

    //根据输入流来创建输出流
    for (i = 0; i < ifmt_ctx->nb_streams; i++) {
        //Create output AVStream according to input AVStream
        AVStream *in_stream = ifmt_ctx->streams[i];
        AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
        if (!out_stream) {
            LOGE("Failed allocating output stream\n");
            ret = AVERROR_UNKNOWN;
            goto end;
        }
        //复制封装格式上下文
        ret = avcodec_copy_context(out_stream->codec, in_stream->codec);
        if (ret < 0) {
            LOGE("Failed to copy context from input to output stream codec context\n");
            goto end;
        }
        out_stream->codec->codec_tag = 0;
        if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER) {
            out_stream->codec->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
        }
    }
    //封装格式
    ofmt = ofmt_ctx->oformat;
    //打开输出文件/URL
    if (!(ofmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&ofmt_ctx->pb, output_str, AVIO_FLAG_WRITE);
        if (ret < 0) {
            LOGE("Could not open output URL '%s'", output_str);
            goto end;
        }
    }
    //写文件头
    ret = avformat_write_header(ofmt_ctx, NULL);
    if (ret < 0) {
        LOGE("Error occurred when opening output URL\n");
        goto end;
    }
    //获取开始时间
    start_time = av_gettime();
    //开始循环读一帧数据
    while (1) {
        AVStream *in_stream, *out_stream;
        //Get an AVPacket
        ret = av_read_frame(ifmt_ctx, &pkt);
        if (ret < 0)
            break;
        //计算帧间隔，参考时钟/采样率
        if (pkt.pts == AV_NOPTS_VALUE) {
            //Write PTS
            AVRational time_base = ifmt_ctx->streams[videoindex]->time_base;
            //Duration between 2 frames (us)
            int64_t calc_duration = (ino64_t) (AV_TIME_BASE /
                                               av_q2d(ifmt_ctx->streams[videoindex]->r_frame_rate));
            //Parameters
            pkt.pts = (int64_t) ((frame_index * calc_duration) /
                                 (av_q2d(time_base) * AV_TIME_BASE));
            pkt.dts = pkt.pts;
            pkt.duration = (int64_t) (calc_duration / (av_q2d(time_base) * AV_TIME_BASE));
        }
        //视频帧之间延时
        if (pkt.stream_index == videoindex) {
            AVRational time_base = ifmt_ctx->streams[videoindex]->time_base;
            AVRational time_base_q = {1, AV_TIME_BASE};
            int64_t pts_time = av_rescale_q(pkt.dts, time_base, time_base_q);
            int64_t now_time = av_gettime() - start_time;
            //延时以保持同步
            if (pts_time > now_time)
                av_usleep((unsigned) (pts_time - now_time));

        }

        in_stream = ifmt_ctx->streams[pkt.stream_index];
        out_stream = ofmt_ctx->streams[pkt.stream_index];

        //pts/dts转换
        pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base,
                                   (AVRounding) (AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
        pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base,
                                   (AVRounding) (AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
        pkt.pos = -1;
        //视频帧计数
        if (pkt.stream_index == videoindex) {
            LOGE("Send %8d video frames to output URL\n", frame_index);
            frame_index++;
        }
        //写一帧数据
        //ret = av_write_frame(ofmt_ctx, &pkt);
        ret = av_interleaved_write_frame(ofmt_ctx, &pkt);

        if (ret < 0) {
            LOGE("Error muxing packet\n");
            break;
        }
        //释放包数据内存
        av_free_packet(&pkt);

    }
    //写文件尾
    av_write_trailer(ofmt_ctx);

    end:
    avformat_close_input(&ifmt_ctx);
    /* close output */
    if (ofmt_ctx && !(ofmt->flags & AVFMT_NOFILE)) {
        avio_close(ofmt_ctx->pb);
    }
    avformat_free_context(ifmt_ctx);
    avformat_free_context(ofmt_ctx);
    env->ReleaseStringUTFChars(inputurl, input_str);
    env->ReleaseStringUTFChars(outputurl, output_str);
    if (ret < 0 && ret != AVERROR_EOF) {
        LOGE("Error occurred.\n");
        return -1;
    }
    return 0;
}

#ifdef __cplusplus
}
#endif