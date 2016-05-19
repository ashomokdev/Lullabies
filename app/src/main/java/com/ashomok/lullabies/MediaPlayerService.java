package com.ashomok.lullabies;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Iuliia on 28.04.2016.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String TAG = MediaPlayerService.class.getSimpleName();

    MediaPlayer mMediaPlayer = null;

    // Binder given to clients
    private final IBinder mBinder = new MediaPlayerServiceBinder();


    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

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

    public void play(int musicResId) {
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
        mMediaPlayer.pause();
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
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
}
