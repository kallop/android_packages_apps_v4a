package com.vipercn.viper4android_v2.activity;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.misc.Data;
import com.vipercn.viper4android_v2.misc.ItemInfo;
import com.vipercn.viper4android_v2.service.UpdateCheckService;

import java.util.LinkedList;

public class OnlineFragment extends Fragment implements ShortcutAdapter.OnRecyclerViewItemClickListener {

    private MainActivity mLauncher;

    private View mEmptyView;
    private View mRootView;

    private ProgressDialog mProgressDialog;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private ShortcutAdapter mShortcutAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int count = intent.getIntExtra(UpdateCheckService.DATA_COUNT, -1);
            if (UpdateCheckService.ACTION_CHECK_FINISHED.equals(action)) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                if (count > 0 && mLauncher.getPrefs("settings").getBoolean("false_content", false)) {
                    mLauncher.mSectionsPagerAdapter.notifyDataSetChanged();
                    mLauncher.getPrefs("settings").edit().putBoolean("false_content", false).commit();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLauncher = (MainActivity) getContext();

        IntentFilter filter = new IntentFilter(UpdateCheckService.ACTION_CHECK_FINISHED);
        getContext().registerReceiver(mReceiver, filter);

        return updateLayout(inflater, container);
    }

    private View updateLayout(LayoutInflater inflater, ViewGroup container) {
        LinkedList<ItemInfo> data = Data.loadData(getContext(), Data.OFFLINE_FILENAME);
        if (data.size() <= 0) {
            mLauncher.getPrefs("settings").edit().putBoolean("false_content", true).commit();
            mEmptyView = inflater.inflate(R.layout.fragment_main, container, false);
            mSwipeRefreshLayout = (SwipeRefreshLayout) mEmptyView.findViewById(R.id.my_swiperefresh_view_empty);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            getAction();
                        }
                    }, 1);
                }
            });
            TextView textView = (TextView) mEmptyView.findViewById(R.id.section_label);
            textView.setText("No content, Pull down refresh!");
            return mEmptyView;
        }

        mRootView = inflater.inflate(R.layout.online_main, container, false);

        mLauncher.getPrefs("settings").edit().putBoolean("false_content", false).commit();

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.my_swiperefresh_view_root);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        getAction();
                    }
                }, 1);
            }
        });

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShortcutAdapter = new ShortcutAdapter(getContext(), data);
        mRecyclerView.setAdapter(mShortcutAdapter);
        mShortcutAdapter.setOnItemClickListener(this);

        return mRootView;
    }

    private void getAction() {
        if (!Utils.isOnline(getContext())) {
            return;
        }

        Data.saveData(getContext(), new LinkedList<ItemInfo>(), Data.OFFLINE_FILENAME);// clear

        Intent checkIntent = new Intent(getContext(), UpdateCheckService.class);
        checkIntent.setAction(UpdateCheckService.ACTION_CHECK);
        getContext().startService(checkIntent);
    }

    @Override
    public void onItemClick(View view , int postion) {
        mShortcutAdapter.notifyDataSetChanged();
    }
}
