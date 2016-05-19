package com.ashomok.lullabies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
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
                showNotification();

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

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                        //icon sizes
//        Color (Enabled): #FFFFFF 80% opacity
//        Color(Disabled):#FFFFFF 30% opacity
//        mdpi    @ 24.00dp   = 24.00px
//        hdpi    @ 24.00d p = 36.00 px
//        xhdpi   @ 24.00dp   = 48.00px
//66 × 66 area in 72 × 72 (xxhdpi)
//        88 × 88 area in 96 × 96 (xxxhdpi)

                .setContentTitle("My notification")
                        .setContentText("Hello World!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, FragmentPagerSupportActivity.class);

//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ResultActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);


        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Sets an ID for the notification
        int mNotificationId = 001;

        // mNotificationId allows you to update the notification later on.
        mNotificationManager.notify(mNotificationId, mBuilder.build());

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