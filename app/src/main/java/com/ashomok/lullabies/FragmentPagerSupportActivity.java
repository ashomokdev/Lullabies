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

//import com.squareup.picasso.Picasso;

//import com.viewpagerindicator.library.*;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends Activity {
    protected static final int NUM_ITEMS = 3;

    private MyAdapter mAdapter;

    private ViewPager mPager;

    private FloatingActionButton fabPlay;
    private FloatingActionButton fabPause;

    private MediaPlayerService mService;
    private boolean mBound = false;
    public static final String PAGE_NUMBER_KEY = "page_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            int startPageNumber;
            boolean isPlaying = false;

            if (savedInstanceState != null) {
                startPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
                isPlaying = true;
            } else {
                startPageNumber = 0;
            }

            setContentView(R.layout.fragment_pager);

            mAdapter = new MyAdapter(getFragmentManager());

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);


//            CircleView circleView = (CircleView) findViewById(R.id.circle_view);
//            circleView.setColorAccent(getResources().getColor(R.color.colorAccent)); //Optional
//            circleView.setColorBase(getResources().getColor(R.color.colorPrimary)); //Optional
//            circleView.setViewPager(pager);

            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

            mPager.setCurrentItem(startPageNumber);

            fabPlay = (FloatingActionButton) findViewById(R.id.play);
            fabPause = (FloatingActionButton) findViewById(R.id.stop);

            instantiateMusicButtonsForPage(startPageNumber);

            if (isPlaying) {
                fabPlay.setVisibility(View.VISIBLE);
                fabPause.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instantiateMusicButtonsForPage(final int pageNumber) {
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicFragmentSettings settings = FragmentFactory.musicFragmentSettingsList.get(pageNumber);
                int track = settings.getTrack();

                mService.play(track, mPager.getCurrentItem());

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