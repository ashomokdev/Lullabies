/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashomok.lullabies.services.playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.ashomok.lullabies.MainActivity;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.services.playback.cache.AlbumArtCache;
import com.ashomok.lullabies.tools.LogHelper;
import com.ashomok.lullabies.tools.ResourceHelper;

import static com.ashomok.lullabies.services.playback.MusicProviderSource.CUSTOM_METADATA_TRACK_IMAGE;


/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. Maintaining a visible notification (usually) guarantees that the music service
 * won't be killed during playback.
 */
public class MediaNotificationManager extends BroadcastReceiver {
    private static final String TAG = LogHelper.makeLogTag(MediaNotificationManager.class);

    private static final int NOTIFICATION_ID = 412;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "com.example.android.uamp.pause";
    public static final String ACTION_PLAY = "com.example.android.uamp.play";
    public static final String ACTION_PREV = "com.example.android.uamp.prev";
    public static final String ACTION_NEXT = "com.example.android.uamp.next";

    private final MusicService mService;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;

    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMetadata;

    private final NotificationManagerCompat mNotificationManager;

    private final PendingIntent mPauseIntent;
    private final PendingIntent mPlayIntent;
    private final PendingIntent mPreviousIntent;
    private final PendingIntent mNextIntent;

    private final int mNotificationColor;

    private boolean mStarted = false;

    public MediaNotificationManager(MusicService service) throws RemoteException {
        mService = service;
        updateSessionToken();

        mNotificationColor = ResourceHelper.getThemeColor(mService, R.attr.colorPrimary,
                Color.DKGRAY);

        mNotificationManager = NotificationManagerCompat.from(service);

        String pkg = mService.getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before {@link #stopNotification} is called.
     */
    public void startNotification() {
        if (!mStarted) {
            mMetadata = mController.getMetadata();
            mPlaybackState = mController.getPlaybackState();

            // The notification must be updated after setting started to true
            Notification notification = createNotification();
            if (notification != null) {
                mController.registerCallback(mCb);
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_NEXT);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PREV);
                mService.registerReceiver(this, filter);

                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    public void stopNotification() {
        if (mStarted) {
            mStarted = false;
            mController.unregisterCallback(mCb);
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mService.stopForeground(true);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "Received intent with action " + action);
        switch (action) {
            case ACTION_PAUSE:
                mTransportControls.pause();
                break;
            case ACTION_PLAY:
                mTransportControls.play();
                break;
            case ACTION_NEXT:
                mTransportControls.skipToNext();
                break;
            case ACTION_PREV:
                mTransportControls.skipToPrevious();
                break;
            default:
                Log.w(TAG, "Unknown intent ignored. Action="+ action);
        }
    }

    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see {@link android.media.session.MediaController.Callback#onSessionDestroyed()})
     */
    private void updateSessionToken() throws RemoteException {
        MediaSessionCompat.Token freshToken = mService.getSessionToken();
        if (mSessionToken == null && freshToken != null ||
                mSessionToken != null && !mSessionToken.equals(freshToken)) {
            if (mController != null) {
                mController.unregisterCallback(mCb);
            }
            mSessionToken = freshToken;
            if (mSessionToken != null) {
                mController = new MediaControllerCompat(mService, mSessionToken);
                mTransportControls = mController.getTransportControls();
                if (mStarted) {
                    mController.registerCallback(mCb);
                }
            }
        }
    }

    private PendingIntent createContentIntent(MediaMetadataCompat metadata) {
        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (metadata != null) {
            openUI.putExtra(MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, metadata.getDescription());
        }
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            mPlaybackState = state;
            Log.d(TAG, "Received new playback state"+ state);
            if (state.getState() == PlaybackStateCompat.STATE_STOPPED ||
                    state.getState() == PlaybackStateCompat.STATE_NONE) {
                stopNotification();
            } else {
                Notification notification = createNotification();
                if (notification != null) {
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMetadata = metadata;
            Log.d(TAG, "Received new metadata "+ metadata);
            Notification notification = createNotification();
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "Session was destroyed, resetting to the new session token");
            try {
                updateSessionToken();
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage() + "could not connect media controller");
            }
        }
    };

    private Notification createNotification() {
        Log.d(TAG, "updateNotificationMetadata. mMetadata=" + mMetadata);
        if (mMetadata == null || mPlaybackState == null) {
            return null;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService);
        int playPauseButtonPosition = 0;

        // If skip to previous action is enabled
        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_previous_white_24dp,
                        mService.getString(R.string.label_previous), mPreviousIntent);

            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1;
        }

        addPlayPauseAction(notificationBuilder);

        // If skip to next action is enabled
        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(R.drawable.ic_skip_next_white_24dp,
                mService.getString(R.string.label_next), mNextIntent);
        }


        int fetchArtID = 0;
        Bitmap art = null;
        int drawableID =  (int)mMetadata.getLong(CUSTOM_METADATA_TRACK_IMAGE);
        if (drawableID != 0) {
            // async fetch the album art icon
            art = AlbumArtCache.getInstance().getBigImage(drawableID);
            if (art == null) {
                fetchArtID = drawableID;
                // use a placeholder art while the remote art is being downloaded
                art = BitmapFactory.decodeResource(mService.getResources(),
                    R.drawable.ic_default_art);
            }
        }

        MediaDescriptionCompat description = mMetadata.getDescription();
        notificationBuilder
                .setStyle(new NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(
                            new int[]{playPauseButtonPosition})  // show only play/pause in compact view
                    .setMediaSession(mSessionToken))
                .setColor(mNotificationColor)
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setContentIntent(createContentIntent(mMetadata))
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setLargeIcon(art);

        setNotificationPlaybackState(notificationBuilder);
        if (fetchArtID != 0) {
            fetchBitmapFromDrawableIdAsync(fetchArtID, notificationBuilder);
        }

        return notificationBuilder.build();
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        Log.d(TAG, "updatePlayPauseAction");
        String label;
        int icon;
        PendingIntent intent;
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = mService.getString(R.string.label_pause);
            icon = R.drawable.uamp_ic_pause_white_24dp;
            intent = mPauseIntent;
        } else {
            label = mService.getString(R.string.label_play);
            icon = R.drawable.uamp_ic_play_arrow_white_24dp;
            intent = mPlayIntent;
        }
        builder.addAction(new NotificationCompat.Action(icon, label, intent));
    }

    private void setNotificationPlaybackState(NotificationCompat.Builder builder) {
        Log.d(TAG, "updateNotificationPlaybackState. mPlaybackState=" + mPlaybackState);
        if (mPlaybackState == null || !mStarted) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!");
            mService.stopForeground(true);
            return;
        }
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                && mPlaybackState.getPosition() >= 0) {
            Log.d(TAG, "updateNotificationPlaybackState. updating playback position to "+
                    (System.currentTimeMillis() - mPlaybackState.getPosition()) / 1000+ " seconds");
            builder
                .setWhen(System.currentTimeMillis() - mPlaybackState.getPosition())
                .setShowWhen(true)
                .setUsesChronometer(true);
        } else {
            Log.d(TAG, "updateNotificationPlaybackState. hiding playback position");
            builder
                .setWhen(0)
                .setShowWhen(false)
                .setUsesChronometer(false);
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING);
    }

    private void fetchBitmapFromDrawableIdAsync(final int drawableID,
                                                final NotificationCompat.Builder builder) {
        AlbumArtCache.getInstance().fetch(drawableID, new AlbumArtCache.FetchListener() {
            @Override
            public void onFetched(int drawableID, Bitmap bitmap, Bitmap icon) {
                if (mMetadata != null && ((int)mMetadata.getLong(CUSTOM_METADATA_TRACK_IMAGE)) == drawableID) {
                    // If the media is still the same, update the notification:
                    Log.d(TAG, "fetchBitmapFromDrawableIdAsync: set bitmap to "+ drawableID);
                    builder.setLargeIcon(bitmap);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        });
    }
}
