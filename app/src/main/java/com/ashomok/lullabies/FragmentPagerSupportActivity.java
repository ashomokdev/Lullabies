package com.ashomok.lullabies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends Activity {
    protected static final int NUM_ITEMS = 3;

    private MyAdapter mAdapter;

    private ViewPager mPager;

    FloatingActionButton fabPlay;
    FloatingActionButton fabPause;

    MediaPlayerService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pager);

            mAdapter = new MyAdapter(getFragmentManager());

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);

            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

            int startPageNumber = 0;
            mPager.setCurrentItem(startPageNumber);

            fabPlay = (FloatingActionButton) findViewById(R.id.play);
            fabPause = (FloatingActionButton) findViewById(R.id.stop);

            instantiateMusicButtonsForPage(startPageNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instantiateMusicButtonsForPage(final int pageNumber) {
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicFragmentSettings settings = MusicFragment.musicFragmentSettingsList.get(pageNumber);
                int track = settings.getTrack();

                mService.play(track);

                fabPause.setVisibility(View.VISIBLE);
                fabPlay.setVisibility(View.GONE);

            }
        });

        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mService.pause();

                fabPlay.setVisibility(View.VISIBLE);
                fabPause.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

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


    //TODO useless?
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
            instantiateMusicButtonsForPage(position);
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
            // We've bound to LocalService, cast the IBinder and get LocalService instance
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