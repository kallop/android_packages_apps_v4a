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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.service.ViPER4AndroidService;

public class MainActivity extends AppCompatActivity {

    private ViPER4AndroidService mAudioServiceInstance;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ArrayAdapter<String> mDeviceAdapter;
    public final String[] DEFAULT_AUDIO_DEVICES = new String[]{
            "headset", "speaker", "bluetooth", "usb"
    };
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public int mCurrentSelectedPosition;

    public final String SHARED_PREFERENCES_BASENAME = "com.vipercn.viper4android_v2";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Spinner mSpinner;

    private Switch mToggleSwitch;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
                    getPrefs("").edit().putString("viper4android.headphonefx.fireq.custom", "4.5;4.5;3.5;1.2;1.0;0.5;1.4;1.75;3.5;2.5;").commit();
                    getPrefs("").edit().putString("viper4android.headphonefx.fireq", "4.5;4.5;3.5;1.2;1.0;0.5;1.4;1.75;3.5;2.5;").commit();
                }
                sendBroadcast(new Intent(ViPER4Android.ACTION_UPDATE_PREFERENCES));
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
                ViPER4AndroidService service = ((ViPER4AndroidService.LocalBinder)binder).getService();
                mAudioServiceInstance = service;
                String routing = ViPER4AndroidService.getAudioOutputRouting(getSharedPreferences(
                        SHARED_PREFERENCES_BASENAME + ".settings", MODE_PRIVATE));
                for (int i = 0; i < DEFAULT_AUDIO_DEVICES.length; i++) {
                     if (routing.equals(DEFAULT_AUDIO_DEVICES[i])) {
                         setSelection(i);
                         break;
                     }
                }
                unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        Intent serviceIntent = new Intent(this, ViPER4AndroidService.class);
        bindService(serviceIntent, connection, Context.BIND_IMPORTANT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
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

    private void setSelection(int Position) {
        mCurrentSelectedPosition = Position;
        mSpinner.setSelection(mCurrentSelectedPosition);
        String config = DEFAULT_AUDIO_DEVICES[mCurrentSelectedPosition];
        SharedPreferences prefSettings = getSharedPreferences(SHARED_PREFERENCES_BASENAME + "." + localizeDeviceConfig(mCurrentSelectedPosition), 0);
        mToggleSwitch.setChecked(getPrefs("").getBoolean("viper4android.global.forceenable.enable", false)
              && getPrefs("").getBoolean(localizeDeviceOnConfig(mCurrentSelectedPosition), false));
    }

    private final String localizeDeviceOnConfig(int Position) {
        return "viper4android." + DEFAULT_AUDIO_DEVICES[Position] + "fx.enable";
    }

    public final String localizeDeviceConfig(int Position) {
        return DEFAULT_AUDIO_DEVICES[Position];
    }

    private final String localizeDevice(String device) {
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CurrencyFragment();
                case 1:
                    return PlaceholderFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Shortcut";
                case 1:
                    return "Online";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
