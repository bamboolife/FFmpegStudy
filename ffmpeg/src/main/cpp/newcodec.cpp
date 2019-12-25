//
// Created by 蒋建伟 on 2019-12-24.
//

#include <jni.h>
#include "logutil.h"

#ifdef __cplusplus
extern "C" {
#endif
//编解码库
#include "libavcodec/avcodec.h"
//封装格式库
#include "libswscale/swscale.h"
// 工具包
#include "libavutil/imgutils.h"
// 视频像素格式
#include "libavformat/avformat.h"



JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoCodec_newdecode(JNIEnv *env, jobject thiz, jstring inputurl,

                                           jstring outputurl) {
    AVFormatContext *formatContext;
    //解码视频的地址
    char input_str[500];
    //解码后文件的存放地址
    char output_str[500];
    //打开文件标识
    int result=-1;
    //查找视频流标识
    int video_stream_result;
    int video_stream_index = -1;
    //打开解码器标识
    int codec_open2_result;
    //流类型
    enum AVMediaType mediatype;

    AVCodecParameters *avcodecParameters ;
    enum AVCodecID codeid;
    AVCodec *codec;
    AVCodecContext *avCodecContext;

    AVPacket *avpacket;
    AVFrame *avFrame_in;

    // 第一步注册组建
    // 旧的接口:av_register_all();
    // 这不需要注册组建
    // 4.0.0 API大量更换，让很多初学者很难去学习
    // 详细接口替换可以查看下载的源码目录下的doc/APIChange 这个文件
    // 具体的用法就需要查看方法注释

    // 第二步：打开封装格式->打开文件
    // 例如：.mp4、.mov、.wmv文件等等...
    // avformat_open_input();
    formatContext=avformat_alloc_context();
    // 视频文件地址和输出地址
    sprintf(input_str,"%s",env->GetStringUTFChars(inputurl,NULL));
    sprintf(output_str,"%s",env->GetStringUTFChars(outputurl,NULL));
    result=avformat_open_input(&formatContext,input_str,NULL,NULL);
    if (result!=0){
        LOGE("Could not open input file.");
        return -1;
    }

    // 第三步：查找视频流
    // 如果是视频解码，那么查找视频流，如果是音频解码，那么就查找音频流
    // avformat_find_stream_info();
    // AVProgram 视频的相关信息
    video_stream_result=avformat_find_stream_info(formatContext,NULL);
    if(video_stream_result<0){
        LOGE("Couldn't find stream information.\n");
        return -1;
    }
    // 第四步：查找视频解码器
    // 1、查找视频流索引位置
    for (int i = 0; i <formatContext->nb_streams ; ++i) {
        // 判断流的类型
        // 旧的接口 formatContext->streams[i]->codec->codec_type
        // 4.0.0以后新加入的类型用于替代codec
        // codec -> codecpar
       mediatype=formatContext->streams[i]->codecpar->codec_type;
       if(mediatype==AVMEDIA_TYPE_VIDEO){
           //视频流
           video_stream_index=i;
           break;
       }else if (mediatype==AVMEDIA_TYPE_AUDIO){
           //音频流
       }else{
           //其它流
       }

    }
    // 2、根据视频流索引，获取解码器上下文
    // 旧的接口 拿到上下文,formatContext->streams[i]->codec
    // 4.0.0以后新加入的类型用于替代codec
    // codec -> codecpar 此处新接口不需要上下文
    avcodecParameters=formatContext->streams[video_stream_index]->codecpar;
    codeid=avcodecParameters->codec_id;
    // 3、根据解码器上下文，获得解码器ID，然后查找解码器
    // avcodec_find_encoder(enum AVCodecID id) 编码器
    //查找video stream 相对应的解码器
    codec=avcodec_find_decoder(codeid);
    if(codec==NULL){
        LOGE("Couldn't find Codec.\n");
        return -1;
    }
    // 第五步：打开解码器
    // avcodec_open2();
    // 旧接口直接使用codec作为上下文传入
    // formatContext->streams[avformat_stream_index]->codec被遗弃
    // 新接口如下
    avCodecContext=avcodec_alloc_context3(NULL);
    if (avCodecContext==NULL){
        LOGE("create codecContext is false");
        return -1;
    }

    // avcodec_parameters_to_context(AVCodecContext *codec, const AVCodecParameters *par)
    // 将新的API中的 codecpar 转成 AVCodecContext

    avcodec_parameters_to_context(avCodecContext,avcodecParameters);
    //打开解码器
    codec_open2_result=avcodec_open2(avCodecContext,codec,NULL);
    if (codec_open2_result!=0){
        LOGE("Couldn't open codec.\n");
        return -1;
    }
    LOGE("decodec name %s",codec->name);
    // 第六步：读取视频压缩数据->循环读取
    // av_read_frame(AVFormatContext *s, AVPacket *pkt)
    // s: 封装格式上下文
    // pkt: 一帧的压缩数据
    avpacket= (AVPacket *)(av_malloc(sizeof(AVPacket)));
    // 用于存放解码之后的像素数据
    avFrame_in=av_frame_alloc();
    int avcodec_receive_frame_result = 0;
    // sws_getContext(int srcW, int srcH, enum AVPixelFormat srcFormat, int dstW, int dstH, enum AVPixelFormat dstFormat, int flags, SwsFilter *srcFilter, SwsFilter *dstFilter, const double *param)
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
    struct SwsContext *swsContext=sws_getContext(avcodecParameters->width,
                                            avcodecParameters->height,
                                            avCodecContext->pix_fmt,
                                             avcodecParameters->width,
                                             avcodecParameters->height,
                                             AV_PIX_FMT_YUV420P,
                                             SWS_BITEXACT,NULL,NULL,NULL);

    // 创建缓冲区
    //创建一个yuv420视频像素数据格式缓冲区(一帧数据)
    AVFrame* avframe_yuv420p = av_frame_alloc();
    //给缓冲区设置类型->yuv420类型
    //得到YUV420P缓冲区大小
    // av_image_get_buffer_size(enum AVPixelFormat pix_fmt, int width, int height, int align)
    //pix_fmt: 视频像素数据格式类型->YUV420P格式
    //width: 一帧视频像素数据宽 = 视频宽
    //height: 一帧视频像素数据高 = 视频高
    //align: 字节对齐方式->默认是1
    int buffer_size = av_image_get_buffer_size(AV_PIX_FMT_YUV420P,
                                               avCodecContext->width,
                                               avCodecContext->height,
                                               1);
    //  开辟一块内存空间
    uint8_t *out_buffer = (uint8_t *)av_malloc(buffer_size);
    //  向avframe_yuv420p->填充数据
    // av_image_fill_arrays(uint8_t **dst_data, int *dst_linesize, const uint8_t *src, enum AVPixelFormat pix_fmt, int width, int height, int align)
    //dst_data: 目标->填充数据(avframe_yuv420p)
    //dst_linesize: 目标->每一行大小
    //src: 原始数据
    //pix_fmt: 目标->格式类型
    //width: 宽
    //height: 高
    //align: 字节对齐方式
    av_image_fill_arrays(avframe_yuv420p->data,
                         avframe_yuv420p->linesize,
                         out_buffer,
                         AV_PIX_FMT_YUV420P,
                         avCodecContext->width,
                         avCodecContext->height,
                         1);

    int y_size, u_size, v_size;
    FILE* file_yuv420p = fopen(output_str,"wb+"); //
    if (file_yuv420p == NULL) {
        printf("Cannot open output file.\n");
        return -1;
    }
    int current_index = 0;
    //从流中读取读取数据到Packet中
    while (av_read_frame(formatContext,avpacket)>=0){
        // 判断是不是视频
        if(avpacket->stream_index==video_stream_index){
            // 每读取一帧数据，立马解码一帧数据
            // 解码之后得到视频的像素数据->YUV
            // avcodec_send_packet(AVCodecContext *avctx, AVPacket *pkt)
            // avctx: 解码器上下文
            // pkt: 获取到数据包
            // 获取一帧数据
           avcodec_send_packet(avCodecContext,avpacket);

           //解码
           avcodec_receive_frame_result=avcodec_receive_frame(avCodecContext,avFrame_in);
           if (avcodec_receive_frame_result==0){
               // 解码成功
               // 此处无法保证视频的像素格式是一定是YUV格式
               // 将解码出来的这一帧数据，统一转类型为YUV
               // sws_scale(struct SwsContext *c, const uint8_t *const *srcSlice, const int *srcStride, int srcSliceY, int srcSliceH, uint8_t *const *dst, const int *dstStride)
               // SwsContext *c: 视频像素格式的上下文
               // srcSlice: 原始视频输入数据
               // srcStride: 原数据每一行的大小
               // srcSliceY: 输入画面的开始位置，一般从0开始
               // srcSliceH: 原始数据的长度
               // dst: 输出的视频格式
               // dstStride: 输出的画面大小
               sws_scale(swsContext,
                         (const uint8_t *const *)avFrame_in->data,
                         avFrame_in->linesize,
                         0,
                         avCodecContext->height,
                         avframe_yuv420p->data,
                         avframe_yuv420p->linesize);
               //方式一：直接显示视频上面去
               //方式二：写入yuv文件格式
               //5、将yuv420p数据写入.yuv文件中
               //5.1 计算YUV大小
               //分析一下原理?
               //Y表示：亮度
               //UV表示：色度
               //有规律
               //YUV420P格式规范一：Y结构表示一个像素(一个像素对应一个Y)
               //YUV420P格式规范二：4个像素点对应一个(U和V: 4Y = U = V)
               y_size = avCodecContext->width * avCodecContext->height;
               u_size = y_size / 4;
               v_size = y_size / 4;
               //5.2 写入.yuv文件
               //首先->Y数据
               fwrite(avframe_yuv420p->data[0], 1, y_size, file_yuv420p);
               //其次->U数据
               fwrite(avframe_yuv420p->data[1], 1, u_size, file_yuv420p);
               //再其次->V数据
               fwrite(avframe_yuv420p->data[2], 1, v_size, file_yuv420p);
               current_index++;
               LOGE("当前解码%d帧",current_index);
           }
        }
    }
    // 第七步：关闭解码器->解码完成
    av_packet_free(&avpacket);
    fclose(file_yuv420p);
    av_frame_free(&avFrame_in);
    av_frame_free(&avframe_yuv420p);
    free(out_buffer);
    avcodec_close(avCodecContext);
    avformat_free_context(formatContext);

    return 0;
}


#ifdef __cplusplus
}
#endif