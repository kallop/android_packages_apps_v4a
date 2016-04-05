package com.vipercn.viper4android_v2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FxApplication extends Application {

    private RequestQueue mRequestQueue;

    private static FxApplication mFxApplication = null;

    public static FxApplication getFxApplication() {
        return mFxApplication;
    }

    @Override
    public void onCreate() {
        mFxApplication = this;
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public RequestQueue getQueue() {
        return mRequestQueue;
    }
}
