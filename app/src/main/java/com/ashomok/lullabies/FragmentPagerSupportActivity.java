package com.ashomok.lullabies;

import android.app.Fragment;
import android.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;

import android.os.IBinder;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

//import com.squareup.picasso.Picasso;

//import com.viewpagerindicator.library.*;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends AppCompatActivity {

    //seems not safe to use
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected static final int NUM_ITEMS = 3;
    private static final String TAG = FragmentPagerSupportActivity.class.getSimpleName();

    private MyAdapter mAdapter;

    private SeekBar volumeSeekbar;

    private ViewPager mPager;

    private ToggleButton fab;
    private ToggleButton volumeButton;

    private AudioManager audioManager;

    private MediaPlayerService mService;
    private boolean mBound = false;
    public static final String PAGE_NUMBER_KEY = "page_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pager);

            int startPageNumber = 0;
            boolean isPlaying = false;

            if (savedInstanceState != null) {
                startPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
                isPlaying = true;
            }

            mAdapter = new MyAdapter(getFragmentManager());
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);


//            CircleView circleView = (CircleView) findViewById(R.id.circle_view);
//            circleView.setColorAccent(getResources().getColor(R.color.colorAccent)); //Optional
//            circleView.setColorBase(getResources().getColor(R.color.colorPrimary)); //Optional
//            circleView.setViewPager(pager);

            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

            mPager.setCurrentItem(startPageNumber);

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            initFab(startPageNumber);
            initSeekbar();
            initVolumeBtn();


            if (isPlaying) {
                fab.setChecked(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVolumeBtn() {
        volumeButton = (ToggleButton) findViewById(R.id.volume_btn);
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

    private void initFab(final int pageNumber) {
        fab = (ToggleButton) findViewById(R.id.fab);
        fab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    // music is playing
                    MusicFragmentSettings settings = FragmentFactory.musicFragmentSettingsList.get(pageNumber);
                    int track = settings.getTrack();
                    mService.play(track, mPager.getCurrentItem());

                } else {

                    mService.pause();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MediaPlayerService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    public static class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {
            if (position < 0) {
                throw new IllegalStateException("Number of page < 0.");
            }
            return FragmentFactory.newInstance(position);
        }
    }

    @Override
    public void onBackPressed() {

        int currentPageNumber = mPager.getCurrentItem();
        if (currentPageNumber > 0) {
            mPager.setCurrentItem(--currentPageNumber);
        } else {
            super.onBackPressed();
        }

    }

    private class OnPageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            initFab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
            mService = binder.getService();
            mBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}