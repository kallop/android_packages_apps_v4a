package com.vipercn.viper4android_v2.activity;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vipercn.viper4android_v2.R;

public class CurrencyFragment extends Fragment implements MyAdapter.OnRecyclerViewItemClickListener {

    private RecyclerView mRecyclerView;
    private ShortcutAdapter mShortcutAdapter;

    private String[] mEqualizerPreset;
    private String[] mEqualizerPresetValues;

    private int[] mPicDataset = {R.drawable.pic_common_style_bg_01, R.drawable.pic_common_style_bg_02, R.drawable.pic_common_style_bg_10, R.drawable.pic_common_style_bg_03, R.drawable.pic_common_style_bg_04, R.drawable.pic_common_style_bg_05, R.drawable.pic_common_style_bg_07, R.drawable.pic_common_style_bg_06};

    private MainActivity mLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currency_main, container, false);

        mLauncher = (MainActivity) getContext();

        // setup equalizer presets
        mEqualizerPreset = getContext().getResources().getStringArray(R.array.equalizer_preset_modes);
        mEqualizerPresetValues = getContext().getResources().getStringArray(R.array.equalizer_preset_values);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mShortcutAdapter = new ShortcutAdapter(getContext(), mEqualizerPreset, mPicDataset);
        mRecyclerView.setAdapter(mShortcutAdapter);
        mAdapter.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(View view, int Position) {
        mLauncher.getPrefs("settings").edit().putInt("home.sound.select", Position).commit();
        mLauncher.getPrefs("").edit().putString("viper4android.headphonefx.fireq.custom", mEqualizerPresetValues[Position]).commit();
        mLauncher.getPrefs("").edit().putString("viper4android.headphonefx.fireq", mEqualizerPresetValues[Position]).commit();
        getContext().sendBroadcast(new Intent(ViPER4Android.ACTION_UPDATE_PREFERENCES));
        mAdapter.notifyDataSetChanged();
    }
}
