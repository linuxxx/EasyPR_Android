#include "EasyPR.h"

#include "easyPR/include/core/plate_locate.h"
#include "easyPR/include/core/plate_judge.h"
#include "easyPR/include/core/chars_segment.h"
#include "easyPR/include/core/chars_identify.h"
#include "easyPR/include/core/plate_detect.h"
#include "easyPR/include/core/chars_recognise.h"
#include "easyPR/include/core/plate_recognize.h"

using namespace easypr;
#include <android/log.h>

#define LOG_TAG "System.out"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT jstring JNICALL Java_com_aiseminar_EasyPR_PlateRecognizer_stringFromJNI(JNIEnv *env, jclass instance) {
    return env->NewStringUTF("Hello from JNI !  Compiled with ABI.");
}

char* jstring2str(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;

	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8"); // "GB2312"
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char *)malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}

JNIEXPORT jlong JNICALL Java_com_aiseminar_EasyPR_PlateRecognizer_initPR(JNIEnv *env, jclass instance, jstring svmpath, jstring annpath) {
//	const string *svm = (*env)->GetStringUTFChars(env, svmpath, 0);
//	const string *ann = (*env)->GetStringUTFChars(env, annpath, 0);
	char* svm = jstring2str(env, svmpath);
	char* ann = jstring2str(env, annpath);

	CPlateRecognize *pr = new CPlateRecognize();

	pr->setDebug(false);
	pr->setLifemode(true);
    pr->setMaxPlates(4);

    pr->loadSVM(svm);
    pr->loadANN(ann);

    return (jlong)pr;
}

JNIEXPORT jlong JNICALL Java_com_aiseminar_EasyPR_PlateRecognizer_uninitPR(JNIEnv *env, jclass instance, jlong recognizerPtr) {
    CPlateRecognize *pr = (CPlateRecognize *)recognizerPtr;
    delete pr;

    return 0;
}

JNIEXPORT jbyteArray JNICALL Java_com_aiseminar_EasyPR_PlateRecognizer_plateRecognize(JNIEnv *env, jclass instance, jlong recognizerPtr, jstring imgpath) {
    CPlateRecognize *pr = (CPlateRecognize *)recognizerPtr;

    //	const string *img = (*env)->GetStringUTFChars(env, imgpath, 0);
    char* img = jstring2str(env, imgpath);
	Mat src = imread(img);

	std::vector<std::string> plateVec;
	int count = pr->plateRecognize(src, plateVec);

	std::string str = "0";
	if(! plateVec.empty() && count == 0) {
		str = plateVec[0];
	}

	char *result = new char[str.length() + 1];
	strcpy(result, str.c_str());
	jbyte *by = (jbyte*) result;
	jbyteArray jarray = env->NewByteArray(strlen(result));
	env->SetByteArrayRegion(jarray, 0, strlen(result), by);

	return jarray;
}