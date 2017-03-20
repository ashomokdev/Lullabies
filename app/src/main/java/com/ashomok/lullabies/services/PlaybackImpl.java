package com.ashomok.lullabies.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.ashomok.lullabies.FragmentData;

import java.io.IOException;

/**
 * Created by iuliia on 2/14/17.
 */

//todo add public class MediaNotificationManager extends BroadcastReceiver
public class PlaybackImpl implements Playback, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private static final String TAG = PlaybackImpl.class.getSimpleName();
    private MediaPlayerServiceTools.MediaPlayerCallback mCallback;
    private int mState;

    public PlaybackImpl(Context context) {
        mContext = context;
    }

    @Override
    public void setCallback(MediaPlayerServiceTools.MediaPlayerCallback callback) {
        mCallback = callback;
    }

    /**
     * Called when media player is done preparing.
     *
     * @see MediaPlayer.OnPreparedListener
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        Log.d(TAG, "onPrepared from MediaPlayer");
        // The media player is done preparing. That means we can start playing.
        startPlaying();

        mState = PlaybackStateCompat.STATE_PLAYING;
    }

    /**
     * Called when media player is done playing current song.
     *
     * @see MediaPlayer.OnCompletionListener
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        Log.d(TAG, "onCompletion from MediaPlayer");
        // The media player finished playing the current song, so we can go ahead
        // and start the next.
        if (mCallback != null) {
            mCallback.onCompletion();
        }
    }

    /**
     * Called when there's an error playing media. When this happens, the media
     * player goes to the Error state. We warn the user about the error and
     * reset the media player.
     *
     * @see MediaPlayer.OnErrorListener
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // The MediaPlayer has moved to the Error state, must be reset!
        mp.reset();
        Log.e(TAG, "Media player error: what=" + what + ", extra=" + extra);
        return true; // true indicates we handled the error
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }


    @Override
    public void start() {
        //redundant
    }

    @Override
    public void play(FragmentData track) {
        if (mState == PlaybackStateCompat.STATE_PAUSED && mMediaPlayer != null) {
            startPlaying();
        } else {
            mState = PlaybackStateCompat.STATE_STOPPED;

            try {
                createMediaPlayerIfNeeded();

                mState = PlaybackStateCompat.STATE_BUFFERING;

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                setMusicSource(track.getTrack());

                // Starts preparing the media player in the background. When
                // it's done, it will call our OnPreparedListener (that is,
                // the onPrepared() method on this class, since we set the
                // listener to 'this'). Until the media player is prepared,
                // we *cannot* call start() on it!
                mMediaPlayer.prepareAsync();

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "Exception playing song" + ex.getMessage());
            }
        }
    }

    @Override
    public void pause() {
        // Pause media player
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            Log.w(TAG, "pause called on non playing player");
        }
        mState = PlaybackStateCompat.STATE_PAUSED;
    }

    @Override
    public void stop() {
        mState = PlaybackStateCompat.STATE_STOPPED;
        releaseMediaPlayer();
    }

    /**
     * Makes sure the media player exists and has been reset. This will create
     * the media player if needed, or reset the existing media player if one
     * already exists.
     */
    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    /**
     * Releases the MediaPlayer.
     */
    private void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer");

        // stop and release the Media Player, if it's available
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void setMusicSource(int musicResId) {
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(musicResId);
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

    private void startPlaying() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mState = PlaybackStateCompat.STATE_PLAYING;
        }
    }
}
