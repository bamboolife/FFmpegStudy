//
// Created by 蒋建伟 on 2019-12-22.
//

#include <jni.h>
#include "logutil.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#ifdef __cplusplus
extern "C" {
#endif
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libswscale/swscale.h"
#include "libavfilter/avfilter.h"
#include "libavutil/imgutils.h"
#include "libavutil/time.h"
//播放倍率
float play_rate = 1;
//视频总时长
int64_t duration = 0;
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_play(JNIEnv *env, jobject thiz, jstring file_path,
                                       jobject surface) {
//    const  char *input_path;
//     input_path=env->GetStringUTFChars(file_path,NULL);
    char *input_path;
    sprintf(input_path,"%s",env->GetStringUTFChars(file_path,NULL));
    //第一步老版本 需要先注册  现在不需要了 可以直接打开文件
    AVFormatContext *avFormatContext;
    if(avformat_open_input(&avFormatContext,input_path,NULL,NULL)!=0){
        LOGD("文件打开失败.\n");
        return -1;
    }
    //第二步查找视频流
    //说明：在一些格式当中没有头部信息，如flv格式，h264格式，这个时候调用avformat_open_input()
    // 在打开文件之后就没有参数，也就无法获取到里面的信息。这个时候就可以调用此函数，因为它会试着去
    // 探测文件的格式，但是如果格式当中没有头部信息，那么它只能获取到编码、宽高这些信息，还是无法获得
    // 总时长。如果总时长无法获取到，则仍需要把整个文件读一遍，计算一下它的总帧数。
    if(avformat_find_stream_info(avFormatContext,NULL)<0){
        LOGD("Couldn't find stream infomation.\n");
        return -1;
    }
    //获取视频总时长
    if(avFormatContext->duration!=AV_NOPTS_VALUE){
       duration=(avFormatContext->duration/AV_TIME_BASE);
      // LOGD("duration=%ld",duration);
    }
    //第三部查找视频解码器
    //1查找视频流索引位置：第一帧
    int video_stream_index=-1;
    for (int i = 0; i <avFormatContext->nb_streams ; ++i) {
         //判断流的类型
         AVMediaType mediaType=avFormatContext->streams[i]->codecpar->codec_type;
         if(mediaType==AVMEDIA_TYPE_VIDEO){
             //视频流
             video_stream_index=i;
         }
    }

    //2根据视频流索引，获取codecpar
     AVCodecParameters *avCodecParameters=avFormatContext->streams[video_stream_index]->codecpar;
   // 根据codecpar，获得解码器ID
    enum AVCodecID avCodecId= avCodecParameters->codec_id;
    //根据解码器id，查找解码器
    AVCodec *codec=  avcodec_find_decoder(avCodecId);
    if (codec==NULL){
        LOGD("Couldn't find Codec.\n");
        return -1;
    }
    //第四部 打开解码器
    //1获取编码器上下文
    AVCodecContext *avCodecContext=avcodec_alloc_context3(codec);
    if(avCodecContext==NULL){
        LOGD("create codecContext is false");
        return -1;
    }
    //2将codecpar转成AVCodecContext 数据填充
    avcodec_parameters_to_context(avCodecContext,avCodecParameters);
    //3开始打开解码器
    if(avcodec_open2(avCodecContext,codec,NULL)!=0){
        LOGD("Couldn't open codec.\n");
        return -1;
    }
    LOGD("decdec name=%s",codec->name);
    //第五步 读取视频压缩数据->循环读取
    //1 实例化封装格式上下文 pkt:一帧的压缩数据
    AVPacket *avPacket=(AVPacket *)av_malloc(sizeof(AVPacket));
    //2分配一个AVFrom:用于存放解码之后的像素数据
    AVFrame *avFrame=av_frame_alloc();
    //初始化一个SwsContext
    // 原始数据
    // scrW: 原始格式宽度
    // scrH: 原始格式高度
    // scrFormat: 原始数据格式
    // 目标数据
    // dstW: 目标格式宽度
    // dstH: 目标格式高度
    // dstFormat: 目标数据格式
    // 当遇到Assertion desc failed at src/libswscale/swscale_internal.h:668
    // 这个问题就是获取元数据的高度有问题
    struct SwsContext *swsContext=sws_getContext(avCodecParameters->width,
                                                 avCodecParameters->height,
                                                 avCodecContext->pix_fmt,
                                                 avCodecParameters->width,
                                                 avCodecParameters->height,
                                                 AV_PIX_FMT_RGBA,
                                                 SWS_BILINEAR,NULL,NULL,NULL);
    AVFrame * avFrameRGBA=av_frame_alloc();
    //给缓冲区设置类型->AV_PIX_FMT_RGBA类型
    //得到RGBA缓冲区大小
    // av_image_get_buffer_size(enum AVPixelFormat pix_fmt, int width, int height, int align)
    //pix_fmt: 视频像素数据格式类型->AV_PIX_FMT_RGBA格式
    //width: 一帧视频像素数据宽 = 视频宽
    //height: 一帧视频像素数据高 = 视频高
    //align: 字节对齐方式->默认是1
    // buffer中数据用于渲染,且格式为RGBA
    int buffer_size = av_image_get_buffer_size(AV_PIX_FMT_RGBA,
                                               avCodecContext->width,
                                               avCodecContext->height,
                                               1);
    //  开辟一块内存空间
    uint8_t *out_buffer = (uint8_t *)av_malloc(buffer_size);
    av_image_fill_arrays(avFrameRGBA->data,
                         avFrameRGBA->linesize,
                         out_buffer,
                         AV_PIX_FMT_RGBA,
                         avCodecContext->width,
                         avCodecContext->height,
                         1);

    // 获取native window
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env, surface);
    //视频缓冲区
    ANativeWindow_Buffer windowBuffer;
    int frameFinished;
    int count;
    while (av_read_frame(avFormatContext,avPacket)>=0){
        //判断是不是视频
        if(avPacket->stream_index==video_stream_index){
            avcodec_send_packet(avCodecContext,avPacket);
            //解码
            frameFinished=avcodec_receive_frame(avCodecContext,avFrame);
            if(frameFinished==0){
                // 设置native window的buffer大小,可自动拉伸
              ANativeWindow_setBuffersGeometry(nativeWindow,avCodecParameters->width,avCodecParameters->height,WINDOW_FORMAT_RGBA_8888);
              //开始绘制
              //先锁定画布
              ANativeWindow_lock(nativeWindow,&windowBuffer,0);
              //转换指定格式
              sws_scale(swsContext,(const uint8_t *const *)avFrame->data,
                      avFrame->linesize,0,avCodecContext->height,
                      avFrameRGBA->data,avFrameRGBA->linesize);
               //获取stride
               //拿到画布的首地址void *dst=window_buffer->bits;
               //the actual bits 实际位置
               uint8_t  *dst= static_cast<uint8_t *>(windowBuffer.bits);
               //拿到一行有多少个像素 ARGBS*4
               //内存地址
               int destStride=windowBuffer.stride*4;
               //像素数据的首地址
               uint8_t  *src=avFrameRGBA->data[0];
               //实际数据在内存中一行的数据量
               int srcStride=avFrameRGBA->linesize[0];
                for (int i = 0; i <avCodecParameters->height ; ++i) {
                    memcpy(dst+i*destStride,src+i*srcStride,srcStride);
                }

                ANativeWindow_unlockAndPost(nativeWindow);
               //保证每秒60针
               av_usleep(1000*16);
               count++;
               LOGD("当前的帧=%d", count);
            }

        }
        av_packet_free(&avPacket);
    }
    //第六步

    av_frame_free(&avFrame);
    av_frame_free(&avFrameRGBA);
    free(out_buffer);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);

    free(swsContext);
    free(&windowBuffer);
    ANativeWindow_release(nativeWindow);
    env->ReleaseStringUTFChars(file_path,input_path);
    return 0;
}

JNIEXPORT void JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_setPlayRate(JNIEnv *env, jobject thiz, jfloat play_rate) {

}

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_filter(JNIEnv *env, jobject thiz, jstring file_path,
                                         jobject surface, jstring filter_type) {
    return 0;
}

JNIEXPORT void JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_again(JNIEnv *env, jobject thiz) {

}


JNIEXPORT void JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_release(JNIEnv *env, jobject thiz) {

}

JNIEXPORT void JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_playAudio(JNIEnv *env, jobject thiz, jboolean play) {

}


#ifdef __cplusplus
}
#endif


