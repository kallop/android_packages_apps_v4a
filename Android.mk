LOCAL_PATH:= $(call my-dir)

#ViPER4Android
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
    frameworks/support/v7/cardview/res frameworks/support/v7/recyclerview/res \
    frameworks/support/v7/appcompat/res frameworks/support/design/res

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := v4a
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_STATIC_JAVA_LIBRARIES := \
    v4a_RootTools \
    v4a_android-support \
    android-support-v4 \
    android-support-v7-cardview \
    android-support-v7-appcompat \
    android-support-design \
    android-support-v7-recyclerview \
    volley

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.cardview \
    --extra-packages android.support.v7.recyclerview \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.design

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
