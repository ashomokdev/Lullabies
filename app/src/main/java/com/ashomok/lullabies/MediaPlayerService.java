package com.ashomok.lullabies;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Iuliia on 28.04.2016.
 */
public class MediaPlayerService extends IntentService {

    private int musicResId;
    private MediaPlayerAsyncTask mediaPlayerAsyncTask;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MediaPlayerService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        musicResId = intent.getIntExtra("music_res_id", 0);

        mediaPlayerAsyncTask = new MediaPlayerAsyncTask(getApplicationContext());
        mediaPlayerAsyncTask.execute(musicResId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
