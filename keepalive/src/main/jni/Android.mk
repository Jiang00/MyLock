LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := daemon_apik
LOCAL_SRC_FILES := daemon_apik.c \
	common.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lm -lz
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := daemon_apil
LOCAL_SRC_FILES := daemon_apil.c \
	common.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lm -lz
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := daemon
LOCAL_SRC_FILES := daemon.c
LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lm -lz
include $(BUILD_EXECUTABLE)

