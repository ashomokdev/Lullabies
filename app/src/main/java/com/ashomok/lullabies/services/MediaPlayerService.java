package com.ashomok.lullabies.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.ashomok.lullabies.FragmentPagerSupportActivity;
import com.ashomok.lullabies.R;


/**
 * Created by Iuliia on 28.04.2016.
 */

class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String TAG = MediaPlayerService.class.getSimpleName();

    private static volatile MediaPlayerService instance;

    MediaPlayer mMediaPlayer;

    public static int mNotificationId = 001;
    private NotificationManager mNotificationManager;

    public static MediaPlayerService getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onCreate() {
        if (instance == null)
            synchronized (MediaPlayerService.class) {
                if (instance == null) {
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.setOnErrorListener(this);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                }
            }
        instance = this;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(mNotificationId);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }


    void cancelNotification() {
        mNotificationManager.cancel(mNotificationId);
    }

    void showNotification(int pageNumber) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.playing));

        Intent resultIntent = new Intent(this, FragmentPagerSupportActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.putExtra(FragmentPagerSupportActivity.PAGE_NUMBER_KEY, pageNumber);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MediaPlayerService.mNotificationId, mBuilder.build());

    }


}
