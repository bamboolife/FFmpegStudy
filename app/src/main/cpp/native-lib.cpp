#include <jni.h>
#include <string>
extern "C"
{
#include <libavcodec/avcodec.h>
JNIEXPORT jstring JNICALL
Java_com_sundy_player_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    using namespace std;

    return env->NewStringUTF(av_version_info());
}

}
