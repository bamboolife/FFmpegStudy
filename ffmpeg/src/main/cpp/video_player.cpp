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

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoPlayer_play(JNIEnv *env, jobject thiz, jstring file_path,
                                       jobject surface) {
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

JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_VideoCodec_decode(JNIEnv *env, jobject thiz, jstring inputurl,
                                        jstring outputurl) {
    return 0;
}

#ifdef __cplusplus
}
#endif


