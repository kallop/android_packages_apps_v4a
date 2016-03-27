LOCAL_PATH:= $(call my-dir)

#ViPER4Android
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

#LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res
LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_PACKAGE_NAME := v4a
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
