package com.ashomok.lullabies;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

/**
 * Created by Iuliia on 28.04.2016.
 */
public class MediaPlayerAsyncTask extends AsyncTask<Integer, Void, Void> {

    private Context context;

    public MediaPlayerAsyncTask(Context context){
        this.context = context;
    }

    private MediaPlayer mediaPlayer;

    @Override
    protected Void doInBackground(Integer... params) {

        mediaPlayer = MediaPlayer.create(context, params[0]);
        mediaPlayer.start();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        releasePlayer();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        releasePlayer();
    }


    protected void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
