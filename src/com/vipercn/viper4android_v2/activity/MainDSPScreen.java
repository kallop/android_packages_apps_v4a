package com.vipercn.viper4android_v2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.preference.EqualizerPreference;
import com.vipercn.viper4android_v2.preference.SummariedListPreference;

public final class MainDSPScreen extends PreferenceFragment {

    public static final String PREF_KEY_EQ = "viper4android.headphonefx.fireq";
    public static final String PREF_KEY_CUSTOM_EQ = "viper4android.headphonefx.fireq.custom";
    public static final String EQ_VALUE_CUSTOM = "custom";
    public static final String PREF_KEY_FORCE = "viper4android.global.forceenable.enable";
    public static final String PREF_KEY_DDC = "viper4android.headphonefx.viperddc.enable";
    public static final String PREF_KEY_VSE = "viper4android.headphonefx.vse.enable";

    private MainActivity mLauncher;

    private EqualizerPreference mEqualizerPreference;

    private final OnSharedPreferenceChangeListener listener
            = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (PREF_KEY_DDC.equals(key)) {
                if (prefs.getBoolean(key, false)) {
                    if (!mLauncher.getPrefs("settings").getBoolean("viper4android.settings.viperddc.notice", false)) {
                    	mLauncher.getPrefs("settings").edit().putBoolean("viper4android.settings.viperddc.notice", true).commit();
	                    AlertDialog.Builder mNotice = new AlertDialog.Builder(getActivity());
	                    mNotice.setTitle("ViPER4Android");
	                    mNotice.setMessage(getActivity().getResources().getString(
	                            R.string.pref_viperddc_tips));
	                    mNotice.setNegativeButton(
	                            getActivity().getResources().getString(R.string.text_ok), null);
	                    mNotice.show();
	                    mNotice = null;
                    }
                }
            }

            if (PREF_KEY_VSE.equals(key)) {
                if (prefs.getBoolean(key, false)) {
                    if (!mLauncher.getPrefs("settings").getBoolean("viper4android.settings.vse.notice", false)) {
                    	mLauncher.getPrefs("settings").edit().putBoolean("viper4android.settings.vse.notice", true).commit();
	                    AlertDialog.Builder mNotice = new AlertDialog.Builder(getActivity());
	                    mNotice.setTitle("ViPER4Android");
	                    mNotice.setMessage(getActivity().getResources().getString(
	                            R.string.pref_vse_tips));
	                    mNotice.setNegativeButton(
	                            getActivity().getResources().getString(R.string.text_ok), null);
	                    mNotice.show();
	                    mNotice = null;
                    }
                }
            }
            mEqualizerPreference = (EqualizerPreference) findPreference(PREF_KEY_CUSTOM_EQ);
            mEqualizerPreference.updateListEqualizerFromValue();
            mEqualizerPreference = null;
            getActivity().sendBroadcast(new Intent(ViPER4Android.ACTION_UPDATE_PREFERENCES));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLauncher = (MainActivity) getContext();

        String config = mLauncher.localizeDeviceConfig(mLauncher.mCurrentSelectedPosition);
        PreferenceManager prefManager = getPreferenceManager();

        prefManager.setSharedPreferencesName(
                ViPER4Android.SHARED_PREFERENCES_BASENAME + "." + config);
        prefManager.setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);
        try {
            int xmlId = R.xml.class.getField(config + "_preferences")
                    .getInt(null);
            addPreferencesFromResource(xmlId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        prefManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
