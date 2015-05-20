LOCAL_PATH := $(call my-dir)/src
include $(CLEAR_VARS)
# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
include /Users/LL/Desktop/ANDROID_SPACE/sdk/native/jni/OpenCV.mk  
FILE_LIST := $(wildcard $(LOCAL_PATH)/core/*.cpp)
LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_MODULE     := imageproc  
LOCAL_LDLIBS += -llog 
include $(BUILD_SHARED_LIBRARY)  

