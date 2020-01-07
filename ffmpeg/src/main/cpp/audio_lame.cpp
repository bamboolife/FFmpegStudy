//
// Created by 蒋建伟 on 2019-12-22.
//
#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_sundy_ffmpeg_AudioPlayer_stop(JNIEnv *env, jobject thiz) {
// TODO: implement stop()
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_setCallback(JNIEnv *env, jobject thiz, jobject push_callback) {
    // TODO: implement setCallback()
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_pushRtmpFile(JNIEnv *env, jobject thiz, jstring path) {
    // TODO: implement pushRtmpFile()
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_sendH264(JNIEnv *env, jobject thiz, jbyteArray buffer,
                                            jint len) {
    // TODO: implement sendH264()
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_initVideo2(JNIEnv *env, jobject thiz, jstring url) {
    // TODO: implement initVideo2()
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sundy_ffmpeg_FFmpegHandle_close(JNIEnv *env, jobject thiz) {
    // TODO: implement close()
    return 0;
}