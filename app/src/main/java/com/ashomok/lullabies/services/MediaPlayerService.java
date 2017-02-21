package com.ashomok.lullabies.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ashomok.lullabies.MainActivity;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.TrackData;

import java.io.Serializable;

import static com.ashomok.lullabies.FragmentFactory.trackDataList;


/**
 * Created by Iuliia on 28.04.2016.
 */

public class MediaPlayerService extends Service{

    public static final String TAG = MediaPlayerService.class.getSimpleName();
    public static final String MUSIC_DATA_ID = "MUSIC_DATA_ID";

    public static int mNotificationId = 1;
    private NotificationManager mNotificationManager;
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_STOP = "STOP";

    private Playback playback;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    //todo try instead binding
//    https://developer.android.com/guide/components/services.html#StartingAService
//    Если служба также не представляет привязку, намерение, доставляемое с помощью startService(), является единственным режимом связи между компонентом приложения и службой. Однако, если вы хотите, чтобы служба оправляла результат обратно, клиент, который запускает службу, может создать объект PendingIntent для сообщения (с помощью getBroadcast()) и доставить его в службу в объекте Intent, который запускает службу. Затем служба может использовать сообщение для доставки результата.
//

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createPlaybackIfNeeded();
    }

    private void createPlaybackIfNeeded() {
        if (playback == null) {
            playback = new PlaybackImpl(this);
            playback.start();
        }
    }

    public void setCallback(@Nullable MediaPlayerServiceTools.MediaPlayerCallback callback) {
        if (callback != null) {
            createPlaybackIfNeeded();
            playback.setCallback(callback);
        }

    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            Log.d(TAG, "onStartCommand with action " + action);
            //todo switch insted ifs
            if (ACTION_PLAY.equals(action)) {
                int trackDataID = startIntent.getIntExtra(MUSIC_DATA_ID, 0);
                showNotification(trackDataID);

                TrackData track = trackDataList.get(trackDataID);
                playback.play(track);

            } else if (ACTION_PAUSE.equals(action)) {

                cancelNotification();
                playback.pause();
            } else if (ACTION_STOP.equals(action)) {

                cancelNotification();
                playback.stop();
            } else {
                Log.i(TAG, "onStartCommand with no action");
            }
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(mNotificationId);
    }

    @Override
    public void onDestroy() {
        if (playback != null) {
            playback.stop();
        }
        cancelNotification();
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }


    void cancelNotification() {
        mNotificationManager.cancel(mNotificationId);
    }

    @SuppressWarnings("deprecation")
    void showNotification(int pageNumber) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.playing));

        Intent resultIntent = new Intent(this, MainActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.putExtra(MainActivity.PAGE_NUMBER_KEY, pageNumber);
        resultIntent.putExtra(MainActivity.IS_PLAYING_KEY, true);

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

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerService.this;
        }
    }
}
