ifeq (1,$(strip $(shell expr $(PLATFORM_SDK_VERSION) \>= 14)))

LOCAL_PATH:= $(call my-dir)

#ViPER4Android
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

#LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res
LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_PACKAGE_NAME := ViPER4Android
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_STATIC_JAVA_LIBRARIES := \
    v4a_RootTools \
    v4a_android-support

ifeq (1,$(strip $(shell expr $(PLATFORM_SDK_VERSION) \>= 23)))
LOCAL_STATIC_JAVA_LIBRARIES += \
    org.apache.http.legacy
endif

include $(BUILD_PACKAGE)

#libs
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    v4a_RootTools:libs/RootTools-4.2.jar \
    v4a_android-support:libs/android-support-v13.jar

include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := libv4a_fx_ics
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib/soundfx
LOCAL_MODULE_SUFFIX := .so
LOCAL_SRC_FILES := assets/libv4a_fx_ics.so
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := audio_effects
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT)/etc
LOCAL_MODULE_SUFFIX := .conf
LOCAL_SRC_FILES := system/etc/audio_effects.conf
include $(BUILD_PREBUILT)

endif
