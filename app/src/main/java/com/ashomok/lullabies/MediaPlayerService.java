package com.ashomok.lullabies;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Iuliia on 28.04.2016.
 */
public class MediaPlayerService extends IntentService {

    private int musicResId;
    private MediaPlayerAsyncTask mediaPlayerAsyncTask;

    public static final String TAG = MediaPlayerService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MediaPlayerService(String name) {
        super(name);
    }

    public MediaPlayerService()
    {
        super("MediaPlayerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        musicResId = intent.getIntExtra("music_res_id", 0);

        mediaPlayerAsyncTask = new MediaPlayerAsyncTask(getApplicationContext());
        mediaPlayerAsyncTask.execute(musicResId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
