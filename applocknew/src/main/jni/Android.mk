LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := core
LOCAL_SRC_FILES := core.cpp sha256.cpp
LOCAL_LDLIBS := -llog
#LOCAL_CFLAGS := -DDEBUG

include $(BUILD_SHARED_LIBRARY)