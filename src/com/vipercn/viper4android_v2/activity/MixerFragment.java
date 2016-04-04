package com.vipercn.viper4android_v2.activity;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.widget.Gallery;

public class MixerFragment extends Fragment {

    private Gallery mEqGallery;

    private MainActivity mLauncher;

    private MainDSPScreen mFragment;
    private FragmentTransaction mFragmentTransaction;

    private String[] mEqualizerPreset;
    private String[] mEqualizerPresetValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mixer_main, container, false);

        mLauncher = (MainActivity) getContext();

        FragmentManager fragmentManager = mLauncher.getFragmentManager();
        mFragmentTransaction = fragmentManager.beginTransaction();
        mFragment = new MainDSPScreen();
        updataFragment();

        mEqGallery = (Gallery) view.findViewById(R.id.eqPresets);
        mEqualizerPreset = getContext().getResources().getStringArray(R.array.equalizer_preset_modes);
        mEqualizerPresetValues = getContext().getResources().getStringArray(R.array.equalizer_preset_values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.equalizer_presets,
                mEqualizerPreset);
        mEqGallery.setAdapter(adapter);
        mEqGallery.setCallbackDuringFling(false);
        mEqGallery.setSelection(mLauncher.getPrefs("settings").getInt("home.sound.select", 0));
        mEqGallery.setOnItemSelectedListener(new Gallery.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                mLauncher.getPrefs("settings").edit().putInt("home.sound.select", position).commit();
                mLauncher.getPrefs("").edit().putString("viper4android.headphonefx.fireq.custom", mEqualizerPresetValues[position]).commit();
                mLauncher.getPrefs("").edit().putString("viper4android.headphonefx.fireq", mEqualizerPresetValues[position]).commit();
            }
        });
        mEqGallery.setEnabled(true);
        return view;
    }

    public void updataFragment() {
        if (!mFragment.isAdded()) {
            mFragmentTransaction.add(R.id.contentPanel, mFragment).commit();
        }
    }
}
