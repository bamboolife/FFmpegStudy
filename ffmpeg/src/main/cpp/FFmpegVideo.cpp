//
// Created by 蒋建伟 on 2019-12-27.
//

#include "FFmpegVideo.h"


FFmpegVideo::FFmpegVideo() {
    this->videoPath=NULL;
}

void FFmpegVideo::setVideoPath(char *videoPath) {
   this->videoPath=videoPath;
}

void FFmpegVideo::play(const char *videoPath) {


}

void FFmpegVideo::pause() {

}

void FFmpegVideo::stop() {

}

void FFmpegVideo::setPlayRate() {

}

uint32_t FFmpegVideo::getDuration() {
    return duration;
}

void FFmpegVideo::releaseFFmpeg() {

}

FFmpegVideo::~FFmpegVideo() {
    releaseFFmpeg();
}
