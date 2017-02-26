package com.ashomok.lullabies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ashomok.lullabies.ad.AdContainer;
import com.ashomok.lullabies.ad.AdMobContainerImpl;
import com.ashomok.lullabies.services.MediaPlayerServiceTools;
import com.ashomok.lullabies.tools.CircleView;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class MainActivity extends AppCompatActivity implements MediaPlayerServiceTools.MediaPlayerCallback {

    //seems not safe to use
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    protected static final int NUM_ITEMS = FragmentFactory.trackDataList.size();

    private static final String TAG = MainActivity.class.getSimpleName();


    public static final String PAGE_NUMBER_KEY = "page_number";
    public static final String IS_PLAYING_KEY = "is_playing";

    private SeekBar volumeSeekbar;
    private ViewPager mPager;
    private ToggleButton fab;

    private AudioManager audioManager;

    private MediaPlayerServiceTools mService;

    private boolean isPlaying;

    private int currentPageNumber;
    private AdContainer adContainer;

    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_activity_layout);

            adContainer = new AdMobContainerImpl(this);
            ViewGroup parent = (ViewGroup) findViewById(R.id.footer_layout);
            adContainer.initAd(parent);

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            currentPageNumber = 0;
            isPlaying = false;

            //never called because of android:configChanges="orientation|keyboardHidden|screenSize"
            //screen rotated
            if (savedInstanceState != null) {
                currentPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
                isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY);
            }

            ObtainSavedStates();

            mService = new MediaPlayerServiceTools(this);
            mService.setCallback(this);

            FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getFragmentManager());
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(currentPageNumber);
            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

            CircleView circleView = (CircleView) findViewById(R.id.circle_view);
            circleView.setColorAccent(getResources().getColor(R.color.colorAccent));
            circleView.setColorBase(getResources().getColor(R.color.colorPrimary));
            circleView.setViewPager(mPager);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            updateFab(currentPageNumber);
            initSeekbar();
            initVolumeBtn();
            initAirplanemodeBtn();


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            AdView ad = (AdView) findViewById(R.id.ad_banner);
            if (ad == null) {
                //ad needs to be loaded
                ViewGroup parent = (ViewGroup) findViewById(R.id.footer_layout);
                adContainer.initAd(parent);
            } else {
                ad.setVisibility(View.VISIBLE);
            }
        } else {
            //hide ad
            AdView ad = (AdView) findViewById(R.id.ad_banner);
            if (ad != null) {
                ad.setVisibility(View.GONE);
            }
        }

    }


    //never called because of android:configChanges="orientation|keyboardHidden|screenSize"
    //todo change architecture. Make service know the plase music pause.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        currentPageNumber = mPager.getCurrentItem();
        savedInstanceState.putInt(PAGE_NUMBER_KEY, currentPageNumber);
        savedInstanceState.putBoolean(IS_PLAYING_KEY, isPlaying);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {

        int currentPageNumber = mPager.getCurrentItem();
        if (currentPageNumber > 0) {

            mPager.setCurrentItem(--currentPageNumber);

        } else {

            ExitDialogFragment exitDialogFragment = ExitDialogFragment.newInstance(R.string.exit_dialog_title);

            exitDialogFragment.show(getFragmentManager(), "dialog");
        }

    }

    @SuppressWarnings("deprecation")
    private void initAirplanemodeBtn() {
        ToggleButton airplanemodeButton = (ToggleButton) findViewById(R.id.airplanemode_btn);
        airplanemodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        // read the airplane mode setting
                        boolean isEnabled = Settings.System.getInt(
                                getContentResolver(),
                                Settings.System.AIRPLANE_MODE_ON, 0) == 1;

                        // toggle airplane mode
                        Settings.System.putInt(
                                getContentResolver(),
                                Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

                        // Post an intent to reload
                        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                        intent.putExtra("state", !isEnabled);
                        sendBroadcast(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    try {
                        Intent intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        try {
                            Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(view.getContext(), R.string.not_able_set_airplane, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void initVolumeBtn() {
        ToggleButton volumeButton = (ToggleButton) findViewById(R.id.volume_btn);
        volumeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            0, 0);
                } else {
                    if (volumeSeekbar != null) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                volumeSeekbar.getProgress(), 0);
                    } else {
                        Log.e(TAG, "volumeSeekbar not initialized, call initSeekbar befor");
                    }
                }

            }
        });
    }

    private void initSeekbar() {
        try {
            volumeSeekbar = (SeekBar) findViewById(R.id.seekbar);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateFab(final int trackID) {
        fab = (ToggleButton) findViewById(R.id.fab);
        fab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //log event for firebase analytics
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(trackID));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                if (isChecked) {
                    try {
                        // music is playing
                        isPlaying = true;
                        mService.handlePlayRequest(trackID);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                } else {

                    isPlaying = false;
                    mService.handleStopRequest();
                }
            }
        });

        if (isPlaying) {
            fab.setChecked(true);
        }
    }

    /**
     * When create Activity after back action in notification - try to obtain previous activity states
     */
    private void ObtainSavedStates() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentPageNumber = extras.getInt(PAGE_NUMBER_KEY);
            try {
                isPlaying = extras.getBoolean(IS_PLAYING_KEY);
            } catch (Exception e) {
                isPlaying = false;
            }
        }
    }

    /**
     * playing music track finished
     */
    @Override
    public void onCompletion() {

        currentPageNumber++;
        if (currentPageNumber >= mPager.getAdapter().getCount()) {
            currentPageNumber = 0;
        }

        mPager.setCurrentItem(currentPageNumber);

        //hack
        fab.setChecked(false);
        fab.setChecked(true);
    }


    private class OnPageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {

            currentPageNumber = position;

            updateFab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * Created by iuliia on 2/6/17.
     */
    public static class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        public FragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.newInstance(position);
        }
    }
}