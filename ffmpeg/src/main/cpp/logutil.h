
#ifndef NATIVE_AUDIO_ANDROID_DEBUG_H_H
#define NATIVE_AUDIO_ANDROID_DEBUG_H_H
#include <android/log.h>

#if 1

#define TAG "MYNDK_TEST"
#define LOGV(...) \
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) \
  __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(...) \
  __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) \
  __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) \
  __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGF(...) \
  __android_log_print(ANDROID_LOG_FATAL, TAG, __VA_ARGS__)

#else

#define LOGV(...)
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#define LOGF(...)
#endif

#endif  // NATIVE_AUDIO_ANDROID_DEBUG_H_H
