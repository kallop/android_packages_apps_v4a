
package com.vipercn.viper4android_v2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.vipercn.viper4android_v2.activity.MainActivity;
import com.vipercn.viper4android_v2.activity.V4AJniInterface;
import com.vipercn.viper4android_v2.service.ViPER4AndroidService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private MainActivity mLauncher;

    @Override
    public void onReceive(Context context, Intent intent) {

        mLauncher = (MainActivity) context;

        boolean bJniLoaded = V4AJniInterface.CheckLibrary();

        boolean bDriverConfigured = mLauncher.getPrefs("settings").getBoolean(
                "viper4android.settings.driverconfigured", false);
        if (bDriverConfigured) {
            context.startService(new Intent(context, ViPER4AndroidService.class));
        } else {
            Log.i("ViPER4Android", "Driver not configured correctly.");
        }
    }
}
