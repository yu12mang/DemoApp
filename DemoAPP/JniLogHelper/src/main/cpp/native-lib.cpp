#include <jni.h>
#include <string>
#include <malloc.h>
#include <stdio.h>
#include<android/log.h>
#define LOG_TAG "jniLog"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_administrator_jnilogtest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGD("loganlog111 content\n");
    LOGD("loganlog222 content\n");
    LOGD("loganlog333 content\n");
    return env->NewStringUTF(hello.c_str());
}
