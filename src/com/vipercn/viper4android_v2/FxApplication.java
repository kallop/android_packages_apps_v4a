package com.vipercn.viper4android_v2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FxApplication extends Application {

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public RequestQueue getQueue() {
        return mRequestQueue;
    }
}
