package com.ashomok.lullabies;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Iuliia on 28.04.2016.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String TAG = MediaPlayerService.class.getSimpleName();

    MediaPlayer mMediaPlayer = null;

    public static int mNotificationId = 001;
    private NotificationManager mNotificationManager;

    // Binder given to clients
    private final IBinder mBinder = new MediaPlayerServiceBinder();


    @Override
    public void onDestroy() {
        cancelNotification();
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }

    private void cancelNotification() {
        mNotificationManager.cancel(mNotificationId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stop();

        stopSelf();
        return false;
    }


    private void setMusicSource(int musicResId) {
        AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(musicResId);
        if (afd == null) {
            return;
        }
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(int musicResId, int pageNumber) {

        showNotification(pageNumber);

        try {
            if (mMediaPlayer.isPlaying()) {
                return;
            }
            mMediaPlayer.reset();

            setMusicSource(musicResId);

            mMediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();

            cancelNotification();
        }
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();

        }
        mMediaPlayer.release();
        mMediaPlayer = null;

        cancelNotification();
    }


    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public class MediaPlayerServiceBinder extends Binder {
        MediaPlayerService getService() {
            // Return this instance of MediaPlayerService so clients can call public methods
            return MediaPlayerService.this;
        }
    }

    private void showNotification(int pageNumber) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(pageNumber + getString(R.string.playing));

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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(mNotificationId);
    }

}
