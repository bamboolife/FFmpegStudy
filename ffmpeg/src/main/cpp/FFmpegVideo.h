//
// Created by 蒋建伟 on 2019-12-27.
//

#ifndef FFMPEGSTUDY_FFMPEGVIDEO_H
#define FFMPEGSTUDY_FFMPEGVIDEO_H

#include <jni.h>
#include "logutil.h"
#include <android/native_window.h>
extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
};
/**
 * 视频编码、视频解码、视频播放、视频暂停、视频停止、分辨率切换、视频播放速度，视频快进和后退，视频截屏、视频转gif
 */
class FFmpegVideo {
private:
    char* videoPath;
    void setVideoPath(char* videoPath);
    uint32_t duration;
    AVFormatContext *avFormatContext;
    uint32_t video_stream_index;
    AVCodecParameters *avCodecParameters;
    AVCodecID avCodecId;
    AVCodec *videoCodec;
    AVCodecContext *avCodecContext;
    void releaseFFmpeg();

public:
    FFmpegVideo();
    ~FFmpegVideo();
    void play(const char* videoPath);
    void pause();
    void stop();


    void setPlayRate();
    uint32_t getDuration();
};


#endif //FFMPEGSTUDY_FFMPEGVIDEO_H
