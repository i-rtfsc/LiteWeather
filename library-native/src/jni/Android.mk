LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS := -Wall -Werror -Wno-unused-parameter -Wno-switch -Wno-unused-function -Wno-unused-variable

LOCAL_SRC_FILES := \
    $(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/*.cpp))

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -landroid

LOCAL_MODULE := native_weather

include $(BUILD_SHARED_LIBRARY)