package com.vipercn.viper4android_v2.activity;

import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.service.ViPER4AndroidService;
import com.vipercn.viper4android_v2.widget.Gallery;

public class MainActivity extends AppCompatActivity {

    private ViPER4AndroidService mViPER4AndroidService;

    private MixerFragment mMixerFragment;
    private OnlineFragment mOnlineFragment;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;

    private ArrayAdapter<String> mDeviceAdapter;

    private ArrayList<Fragment> Fragments;

    public final String[] DEFAULT_AUDIO_DEVICES = new String[]{
            "headset", "speaker", "bluetooth", "usb"
    };
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public int mCurrentSelectedPosition;

    public static final String SHARED_PREFERENCES_BASENAME = "com.vipercn.viper4android_v2";

    public static final String ACTION_UPDATE_PREFERENCES = "com.vipercn.viper4android_v2.UPDATE";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Spinner mSpinner;

    private Switch mToggleSwitch;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ViPER4AndroidService.ACTION_UPDATE_ALL_UI)) {
                for (int i = 0; i < DEFAULT_AUDIO_DEVICES.length; i++) {
                     if (mViPER4AndroidService.getAudioOutputRouting().equals(DEFAULT_AUDIO_DEVICES[i])) {
                         setSelection(i);
                         break;
                     }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare ViPER-DDC database
        if (checkDDCDBVer()) {
        	if (DDCDatabase.initializeDatabase(this))
        		setDDCDBVer();
        }

        // We should start the background service first
        Intent serviceIntent = new Intent(this, ViPER4AndroidService.class);
        startService(serviceIntent);

        setContentView(R.layout.activitys_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMixerFragment = new MixerFragment();
        mOnlineFragment = new OnlineFragment();
        Fragments = new ArrayList<Fragment>();
        Fragments.add(mMixerFragment);
        Fragments.add(mOnlineFragment);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), Fragments);

        mSpinner = (Spinner) findViewById(R.id.device);
        String[] Devices = new String[DEFAULT_AUDIO_DEVICES.length];
        for (int i = 0; i < Devices.length; i++) {
            Devices[i] = localizeDevice(DEFAULT_AUDIO_DEVICES[i]);
        }
        mDeviceAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, Devices);
        mSpinner.setAdapter(mDeviceAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView parent, View view, int position, long id) {
                 TextView v1 = (TextView) view;
                 v1.setTextColor(Color.WHITE);
                 setSelection(position);
                 mSectionsPagerAdapter.notifyDataSetChanged();
             }

             @Override
             public void onNothingSelected(AdapterView parent) {}
        });

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        // setup actionbar on off switch.
        mToggleSwitch = (Switch) findViewById(R.id.switch_on);
        mToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                // set parameter and state
                getPrefs("").edit().putBoolean(localizeDeviceOnConfig(mCurrentSelectedPosition), isChecked).commit();
                getPrefs("").edit().putBoolean("viper4android.global.forceenable.enable", isChecked).commit();
                getPrefs("").edit().putBoolean("viper4android.headphonefx.fireq.enable", isChecked).commit();
                if (TextUtils.isEmpty(getPrefs("").getString("viper4android.headphonefx.fireq.custom", ""))
                    && TextUtils.isEmpty(getPrefs("").getString("viper4android.headphonefx.fireq", ""))) {
                    getPrefs("").edit().putBoolean("viper4android.headphonefx.fireq.enable", isChecked).commit();
                    getPrefs("").edit().putString("viper4android.headphonefx.fireq.custom", "4.5;4.5;3.5;1.2;1.0;0.5;1.4;1.75;3.5;2.5;").commit();
                    getPrefs("").edit().putString("viper4android.headphonefx.fireq", "4.5;4.5;3.5;1.2;1.0;0.5;1.4;1.75;3.5;2.5;").commit();
                }
                sendBroadcast(new Intent(ACTION_UPDATE_PREFERENCES));
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), ActivityMusic.class));
                throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
            }
        });
      
    }

    @Override
    public void onResume() {
        super.onResume();
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mViPER4AndroidService = ((ViPER4AndroidService.LocalBinder)binder).getService();
                String routing = mViPER4AndroidService.getAudioOutputRouting();
                for (int i = 0; i < DEFAULT_AUDIO_DEVICES.length; i++) {
                     if (routing.equals(DEFAULT_AUDIO_DEVICES[i])) {
                         setSelection(i);
                         break;
                     }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mViPER4AndroidService = null;
            }
        };
        Intent serviceIntent = new Intent(this, ViPER4AndroidService.class);
        bindService(serviceIntent, connection, Context.BIND_IMPORTANT);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ViPER4AndroidService.ACTION_UPDATE_ALL_UI);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    public static boolean isDriverCompatible(String szDrvVersion){
    	List<String> lstCompatibleList = new ArrayList<String>();
    	// TODO: <DO NOT REMOVE> add compatible driver version to lstCompatibleList

    	if (lstCompatibleList.contains(szDrvVersion)) {
    		lstCompatibleList.clear();
    		lstCompatibleList = null;
    		return true;
    	} else {
    		lstCompatibleList.clear();
    		lstCompatibleList = null;
    		// Since we cant use getPackageManager in static method, we need to type the current version here
    		// TODO: <DO NOT REMOVE> please make sure this string equals to current apk's version
    		if (szDrvVersion.equals("2.3.4.0")) {
    			return true;
    		}
    		return false;
    	}
    }

    private boolean checkDDCDBVer() {
        PackageManager packageMgr = getPackageManager();
        PackageInfo packageInfo;
        String mVersion;
        try {
            packageInfo = packageMgr.getPackageInfo(getPackageName(), 0);
            mVersion = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return false;
        }
        String mDBVersion = getPrefs("settings").getString("viper4android.settings.ddc_db_compatible", "");
        return mDBVersion == null || mDBVersion.equals("")
                || !mDBVersion.equalsIgnoreCase(mVersion);
    }

    private void setDDCDBVer() {
        PackageManager packageMgr = getPackageManager();
        PackageInfo packageInfo;
        String mVersion;
        try {
            packageInfo = packageMgr.getPackageInfo(getPackageName(), 0);
            mVersion = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return;
        }
        getPrefs("settings").edit().putString("viper4android.settings.ddc_db_compatible", mVersion).commit();
    }

    public SharedPreferences getPrefs(String more) {
        return getSharedPreferences(SHARED_PREFERENCES_BASENAME + "." + (TextUtils.isEmpty(more) ? localizeDeviceConfig(mCurrentSelectedPosition) : more), 0);
    }

    public void setSelection(int Position) {
        mCurrentSelectedPosition = Position;
        mSpinner.setSelection(mCurrentSelectedPosition);
        String config = DEFAULT_AUDIO_DEVICES[mCurrentSelectedPosition];
        mToggleSwitch.setChecked(getPrefs("").getBoolean("viper4android.global.forceenable.enable", false)
              && getPrefs("").getBoolean(localizeDeviceOnConfig(mCurrentSelectedPosition), false));
    }

    private final String localizeDeviceOnConfig(int Position) {
        return "viper4android." + DEFAULT_AUDIO_DEVICES[Position] + "fx.enable";
    }

    public final String localizeDeviceConfig(int Position) {
        return DEFAULT_AUDIO_DEVICES[Position];
    }

    public final String localizeDevice(String device) {
        return getString(getResources().getIdentifier(device + "_title", "string", getPackageName()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    
  

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fm = (fragments.get(position));
            super.instantiateItem(container, position);
            return fm;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mixer";
                case 1:
                    return "Online";
            }
            return null;
        }
    }
}
