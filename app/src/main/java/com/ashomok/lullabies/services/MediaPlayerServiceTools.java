package com.ashomok.lullabies.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.Serializable;

import static com.ashomok.lullabies.services.MediaPlayerService.ACTION_PAUSE;
import static com.ashomok.lullabies.services.MediaPlayerService.ACTION_PLAY;
import static com.ashomok.lullabies.services.MediaPlayerService.ACTION_STOP;
import static com.ashomok.lullabies.services.MediaPlayerService.MUSIC_DATA_ID;

/**
 * Created by Iuliia on 20.06.2016.
 */

//todo Don't be noisy https://developer.android.com/guide/topics/media-apps/volume-and-earphones.html#becoming-noisy
public class MediaPlayerServiceTools {
    private static final String TAG = MediaPlayerServiceTools.class.getSimpleName();

    private Context context;
    private boolean mBound = false;
    private MediaPlayerService mService;
    private MediaPlayerCallback callback;

    public MediaPlayerServiceTools(Context context) {
        this.context = context.getApplicationContext();

        Intent intent = new Intent(context, MediaPlayerService.class);
        context.startService(intent);

    }

    public void setCallback(MediaPlayerCallback callback) {
        this.callback = callback;
    }

    public void destroy() {
        // Unbind from the service
        if (mBound) {
            context.getApplicationContext().unbindService(mConnection);
            mBound = false;
        }

        //stop service
        Intent intent = new Intent(context, MediaPlayerService.class);
        context.stopService(intent);
    }

    /**
     * Handle a request to play music
     */
    public void handlePlayRequest(int trackID) {
        Log.d(TAG, "handlePlayRequest");
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(MUSIC_DATA_ID, trackID);
        context.startService(intent);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Handle a request to pause music
     */
    public void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest");
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(ACTION_PAUSE);
        context.startService(intent);
    }

    /**
     * Handle a request to stop music
     */
    public void handleStopRequest() {
        Log.d(TAG, "handleStopRequest");
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (callback != null) {
                mService.setCallback(callback);
            } else {
                Log.i(TAG, "Service initialized without callback");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mBound = false;
            mService = null;
        }
    };

    public interface MediaPlayerCallback {

        // The media player finished playing the current song, so we go ahead
        // and start the next.
        void onCompletion();
    }


}
