package com.ashomok.lullabies;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Iuliia on 28.04.2016.
 */

//TODO implement stop option
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private int musicResId;

    public static final String TAG = MediaPlayerService.class.getSimpleName();

    MediaPlayer mMediaPlayer = null;




    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicResId = intent.getIntExtra("music_res_id", 0);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(musicResId);
        if (afd == null) {
            return START_NOT_STICKY; //tells the OS to not bother recreating the service again if it was stopped by OS (low memory, etc)
        }
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(this);

        mMediaPlayer.prepareAsync(); // prepare async to not block main thread

        return START_NOT_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }
}
