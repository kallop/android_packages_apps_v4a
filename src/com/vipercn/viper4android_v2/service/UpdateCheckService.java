package com.vipercn.viper4android_v2.service;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.activity.Utils;
import com.vipercn.viper4android_v2.FxApplication;
import com.vipercn.viper4android_v2.requests.GetJsonObjectRequest;
import com.vipercn.viper4android_v2.misc.Data;
import com.vipercn.viper4android_v2.misc.ItemInfo;

public class UpdateCheckService extends IntentService
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String TAG = "UpdateCheckService";

    // request actions
    public static final String ACTION_CHECK = "com.vipercn.viper4android_v2.action.CHECK";
    public static final String ACTION_CANCEL_CHECK = "com.vipercn.viper4android_v2.action.CANCEL_CHECK";

    // broadcast actions
    public static final String ACTION_CHECK_FINISHED = "com.vipercn.viper4android_v2.action.UPDATE_CHECK_FINISHED";
    // extra for ACTION_CHECK_FINISHED: total amount of found updates
    public static final String DATA_COUNT = "update_count";

    // DefaultRetryPolicy values for Volley
    private static final int UPDATE_REQUEST_TIMEOUT = 5000; // 5 seconds
    private static final int UPDATE_REQUEST_MAX_RETRIES = 3;

    public UpdateCheckService() {
        super("UpdateCheckService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (TextUtils.equals(intent.getAction(), ACTION_CANCEL_CHECK)) {
            ((FxApplication) getApplicationContext()).getQueue().cancelAll(TAG);
            return START_NOT_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!Utils.isOnline(this)) {
            // Only check for updates if the device is actually connected to a network
            Log.i(TAG, "Could not check for updates. Not connected to the network.");
            return ;
        }
        getAvailableUpdates();
    }

    private URI getServerURI() {
        String configUpdateUri = getString(R.string.conf_server_url_def);
        return URI.create(configUpdateUri);
    }

    private void getAvailableUpdates() {
        URI updateServerUri = getServerURI();
        GetJsonObjectRequest request;
        try {
            request = new GetJsonObjectRequest(updateServerUri.toASCIIString(),
                    "", buildUpdateRequest(), this, this);
            request.setRetryPolicy(new DefaultRetryPolicy(UPDATE_REQUEST_TIMEOUT,
                        UPDATE_REQUEST_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setTag(TAG);
        } catch (JSONException e) {
            Log.e(TAG, "Could not build request", e);
            return;
        }

        ((FxApplication) getApplicationContext()).getQueue().add(request);
    }

    private JSONObject buildUpdateRequest() throws JSONException {
        JSONObject params = new JSONObject();
        params.put("device", "bacon");

        JSONObject request = new JSONObject();
        request.put("params", params);

        return request;
    }

    private LinkedList<ItemInfo> parseJSON(String jsonString) {
        LinkedList<ItemInfo> updates = new LinkedList<ItemInfo>();
        try {
            JSONObject result = new JSONObject(jsonString);
            JSONArray updateList = result.getJSONArray("result");
            int length = updateList.length();

            Log.d(TAG, "Got JSON data with " + length + " entries");

            for (int i = 0; i < length; i++) {
                if (updateList.isNull(i)) {
                    continue;
                }
                JSONObject item = updateList.getJSONObject(i);
                ItemInfo info = parseUpdateJSONObject(item);
                if (info != null) {
                    updates.add(info);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error in JSON result", e);
        }
        return updates;
    }

    private ItemInfo parseUpdateJSONObject(JSONObject obj) throws JSONException {
        ItemInfo ui = new ItemInfo.Builder()
                .setUserLogo(obj.getString("user_logo"))
                .setUser(obj.getString("user"))
                .setIntroduce(obj.getString("introduce"))
                .setPicture(obj.getString("picture"))
                .setConfigure(obj.getString("configure"))
                .build();
        return ui;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        VolleyLog.e("Error: ", volleyError.getMessage());
        VolleyLog.e("Error type: " + volleyError.toString());
        Intent intent = new Intent(ACTION_CHECK_FINISHED);
        sendBroadcast(intent);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
            Log.i(TAG, jsonObject.toString());

        LinkedList<ItemInfo> updates = parseJSON(jsonObject.toString());

        Intent intent = new Intent(ACTION_CHECK_FINISHED);
        intent.putExtra(DATA_COUNT, updates.size());

        sendBroadcast(intent);
        Data.saveData(this, updates, Data.OFFLINE_FILENAME);
    }
}
