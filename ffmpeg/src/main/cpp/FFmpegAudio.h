//
// Created by 蒋建伟 on 2019-12-27.
//

#ifndef FFMPEGSTUDY_FFMPEGAUDIO_H
#define FFMPEGSTUDY_FFMPEGAUDIO_H

/**
 * 声音的解码、声音的编码、声音的声道切换、声音的播放、声音的暂停、声音的停止、声音的快进和快退
 */
class FFmpegAudio {
private:
    char* audioPath;
    char* setAudioPath(char* audioPath);

public:
    void play(const char* audioPath);
};


#endif //FFMPEGSTUDY_FFMPEGAUDIO_H
