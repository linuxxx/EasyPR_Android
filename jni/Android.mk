LOCAL_PATH := $(call my-dir)/src
include $(CLEAR_VARS)  
include D:/OpenCV-2.4.10-android-sdk/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk  
FILE_LIST := $(wildcard $(LOCAL_PATH)/core/*.cpp)
LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_MODULE     := imageproc  
LOCAL_LDLIBS += -llog 
include $(BUILD_SHARED_LIBRARY)  

