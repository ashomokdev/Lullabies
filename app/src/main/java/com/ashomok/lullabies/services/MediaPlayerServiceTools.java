package com.ashomok.lullabies.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import com.ashomok.lullabies.tools.TaskDelegate;

import java.io.IOException;

/**
 * Created by Iuliia on 20.06.2016.
 */
public class MediaPlayerServiceTools {
    private static final String TAG = MediaPlayerServiceTools.class.getSimpleName();

    private static volatile MediaPlayerServiceTools instance;

    private Context context;
    private MediaPlayer mMediaPlayer;

    private MediaPlayerServiceTools(Context context, TaskDelegate delegate) {
        this.context = context;
        this.delegate = delegate;

        Intent intent = new Intent(context, MediaPlayerService.class);
        context.startService(intent);
    }

    private final TaskDelegate delegate;

    public static MediaPlayerServiceTools getInstance(Context context, TaskDelegate delegate) {
        if (instance == null)
            synchronized (MediaPlayerServiceTools.class) {
                if (instance == null) {
                    instance = new MediaPlayerServiceTools(context, delegate);
                }
            }
        return instance;
    }

    public static MediaPlayerServiceTools getInstance(Context context) {
        return getInstance(context, null);
    }

    private void setMusicSource(int musicResId) {
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(musicResId);
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

    private void initMediaPlayer() {

        mMediaPlayer = MediaPlayerService.getInstance().mMediaPlayer;

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                delegate.TaskCompletionResult();

            }
        });
    }

    public void play(int musicResId, int pageNumber) {

        initMediaPlayer();

        MediaPlayerService.getInstance().showNotification(pageNumber);

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

            MediaPlayerService.getInstance().cancelNotification();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();

            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        MediaPlayerService.getInstance().cancelNotification();
    }

    public void distroy() {

        stop();

        Intent intent = new Intent(context, MediaPlayerService.class);
        context.stopService(intent);
    }

}
