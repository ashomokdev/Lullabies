package com.ashomok.lullabies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ashomok.lullabies.ad.AdContainer;
import com.ashomok.lullabies.services.playback.MediaBrowserManager;
import com.ashomok.lullabies.services.playback.MusicService;
import com.ashomok.lullabies.services.playback.cache.AlbumArtCache;
import com.ashomok.lullabies.tools.CircleView;
import com.ashomok.lullabies.tools.FABReval;
import com.ashomok.lullabies.tools.LogHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.ashomok.lullabies.services.playback.MediaIDHelper.MEDIA_ID_ROOT;
import static com.ashomok.lullabies.services.playback.MusicProviderSource.CUSTOM_METADATA_TRACK_IMAGE;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class MainActivity extends AppCompatActivity implements MediaBrowserManager.MediaListener {

    public static final String PARENT_MEDIA_ID = "__BY_GENRE__/Lullabies";
    private static final int FRAGMENTS_COUNT = 9; //// TODO: 4/18/17 change to 10

    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;


    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;
    private TextView mLine1Playing;
    private TextView mLine2Playing;
    private TextView mLine3Playing;
    private TextView mLine1Waiting;
    private TextView mLine2Waiting;
    private ProgressBar mLoading;
    private View mControllers;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private ViewPager mPager;
    private FABReval fab;
    private MediaBrowserManager mediaBrowserManager;


    public static final String PAGE_NUMBER_KEY = "page_number";
    public static final String PLAYER_WAS_OPENED = "is_playing";

    private boolean playerWasOpened;
    public int mCurrentPageNumber;

    private AdContainer adContainer;
    private FirebaseAnalytics mFirebaseAnalytics;

    private final Handler mHandler = new Handler();
    private MediaBrowserCompat mMediaBrowser;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackStateCompat mLastPlaybackState;
    public String ParentMediaID;

    /**
     * Optionally used to carry a MediaDescription to
     * the {@link MainActivity}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "CURRENT_MEDIA_DESCRIPTION";
    public static final String EXTRA_CURRENT_PAGE_NUMBER = "EXTRA_CURRENT_PAGE_NUMBER";

    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackstate changed " + state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                Log.d(TAG, "onMetadataChanged");
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        Log.e(TAG, e.getMessage() + " could not connect media controller");
                    }
                }
            };


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_activity_layout);

            //todo // FIXME: 2/27/17
//            adContainer = new AdMobContainerImpl(this);
//            ViewGroup parent = (ViewGroup) findViewById(R.id.footer_layout);
//            adContainer.initAd(parent);

            // Obtain the FirebaseAnalytics instance
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            initializeToolbar();

            mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);
            mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
            mPlayPause = (ImageView) findViewById(R.id.play_pause);
            mSkipNext = (ImageView) findViewById(R.id.next);
            mSkipPrev = (ImageView) findViewById(R.id.prev);
            mStart = (TextView) findViewById(R.id.startText);
            mEnd = (TextView) findViewById(R.id.endText);
            mSeekbar = (SeekBar) findViewById(R.id.seekBar1);
            mLine1Playing = (TextView) findViewById(R.id.line1);
            mLine2Playing = (TextView) findViewById(R.id.line2);
            mLine3Playing = (TextView) findViewById(R.id.line3);
            mLine1Waiting = (TextView) findViewById(R.id.name);
            mLine2Waiting = (TextView) findViewById(R.id.genre);

            mLoading = (ProgressBar) findViewById(R.id.progressBar1);
            mControllers = findViewById(R.id.controllers);
            initPlayerButoonUI();

            mSkipNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaControllerCompat.TransportControls controls =
                            getSupportMediaController().getTransportControls();
                    controls.skipToNext();
                    if (++mCurrentPageNumber < mPager.getAdapter().getCount()) {
                        mPager.setCurrentItem(mCurrentPageNumber, true);
                    }
                }
            });

            mSkipPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaControllerCompat.TransportControls controls =
                            getSupportMediaController().getTransportControls();
                    controls.skipToPrevious();
                    if (--mCurrentPageNumber >= 0) {
                        mPager.setCurrentItem(mCurrentPageNumber, true);
                    }
                }
            });

            mPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePlayPauseCall();
                }
            });

            mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mStart.setText(DateUtils.formatElapsedTime(progress / 1000));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    stopSeekbarUpdate();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    getSupportMediaController().getTransportControls().seekTo(seekBar.getProgress());
                    scheduleSeekbarUpdate();
                }
            });


            mMediaBrowser = new MediaBrowserCompat(this,
                    new ComponentName(this, MusicService.class), mConnectionCallback, null);

            mCurrentPageNumber = 0;

            //init pager
            mPager = (ViewPager) findViewById(R.id.pager);
            FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getFragmentManager());
            mPager.setAdapter(mAdapter);
            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());
            CircleView circleView = (CircleView) findViewById(R.id.circle_view);
            circleView.setColorAccent(getResources().getColor(R.color.colorAccent));
            circleView.setColorBase(getResources().getColor(R.color.colorPrimary));
            circleView.setViewPager(mPager);

            applySavedStates(savedInstanceState);

            ParentMediaID = MEDIA_ID_ROOT;
            Log.d(TAG, "onCreate completed");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initPlayerButoonUI() {
        fab = (FABReval) findViewById(R.id.fab);
        makePlayerClosed();
    }

    private void handlePlayPauseCall() {
        PlaybackStateCompat state = getSupportMediaController().getPlaybackState();
        if (state != null) {
            MediaControllerCompat.TransportControls controls =
                    getSupportMediaController().getTransportControls();
            Log.d(TAG, "handlePlayPauseCall with state " + state.getState());
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_NONE:
                    controls.play();
                    scheduleSeekbarUpdate();
                    break;
                case PlaybackStateCompat.STATE_PLAYING: // fall through
                case PlaybackStateCompat.STATE_BUFFERING:
                    controls.pause();
                    stopSeekbarUpdate();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    controls.play();
                    scheduleSeekbarUpdate();
                    break;
                case PlaybackStateCompat.STATE_ERROR:
                    Log.e(TAG, state.getErrorMessage().toString());
                    break;
                default:
                    break;
            }
        }
    }

    //// TODO: 4/24/17 delete reduntant
//    private void updateFromParams(Intent intent) {
//        if (intent != null) {
//            MediaDescriptionCompat description = intent.getParcelableExtra(
//                    MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
//            if (description != null) {
//                updateMediaDescription(description);
//            }
//        }
//    }

    /**
     * make music_playing and music_not_playing have the same height
     */
    private void resizeBottomLayout() {
        final View musicPlaying = findViewById(R.id.music_playing);
        musicPlaying.post(new Runnable() {
            @Override
            public void run() {
                final int height = musicPlaying.getHeight();

                final View musicNotPlaying = findViewById(R.id.player_waiting);
                musicNotPlaying.post(new Runnable() {

                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams params =
                                (RelativeLayout.LayoutParams) musicNotPlaying.getLayoutParams();
                        params.height = height;
                        musicNotPlaying.setLayoutParams(params);
                        musicNotPlaying.postInvalidate();
                    }
                });
            }
        });
    }

    private void fetchImageAsync(@NonNull MediaMetadataCompat metadata) {
        final ImageView image = (ImageView) findViewById(R.id.image);
        if (image == null) {
            Log.w(TAG, "image view not found.");
            return;
        }

        Bitmap art = null;
        final int mCurrentDrawableId = (int) metadata.getLong(CUSTOM_METADATA_TRACK_IMAGE);
        if (mCurrentDrawableId != 0) {
            // async fetch the album art icon
            AlbumArtCache cache = AlbumArtCache.getInstance();
            art = cache.getBigImage(mCurrentDrawableId);
            if (art == null) {
                // use a placeholder art while the remote art is being downloaded
                art = metadata.getDescription().getIconBitmap();
                if (art == null) {
                    art = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_default_art);
                }
            }
            if (art != null) {
                // if we have the art cached or from the MediaDescription, use it:
                image.setImageBitmap(art);
            } else {
                // otherwise, fetch a high res version and update:
                cache.fetch(mCurrentDrawableId, new AlbumArtCache.FetchListener() {
                    @Override
                    public void onFetched(int drawableId, Bitmap bitmap, Bitmap iconImage) {
                        // sanity check, in case a new fetch request has been done while
                        // the previous hasn't yet returned:
                        if (drawableId == mCurrentDrawableId) {
                            image.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }
    }

    //todo remove - redundant. using only for ads
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            AdView ad = (AdView) findViewById(R.id.ad_banner);
//            if (ad == null) {
//                //ad needs to be loaded
//                ViewGroup parent = (ViewGroup) findViewById(R.id.footer_layout);
//                adContainer.initAd(parent);
//            } else {
//                ad.setVisibility(View.VISIBLE);
//            }
//        } else {
//            //hide ad
//            AdView ad = (AdView) findViewById(R.id.ad_banner);
//            if (ad != null) {
//                ad.setVisibility(View.GONE);
//            }
//        }
//
//    }

    protected void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id " +
                    "'toolbar'");
        }
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }

        onMediaControllerConnected();
    }

    private void onMediaControllerConnected() {
        mediaBrowserManager.onConnected();
        Log.d(TAG, "onMediaControllerConnected");
    }

    //todo remove - redundant?
//    private void updateFromParams(Intent intent) {
//        if (intent != null) {
//            MediaDescriptionCompat description = intent.getParcelableExtra(
//                    MusicPlayerActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
//            if (description != null) {
//                updateMediaDescription(description);
//            }
//        }
//    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        resizeBottomLayout();
        if (mMediaBrowser != null) {
            try {
                mMediaBrowser.connect();
                mediaBrowserManager = new MediaBrowserManager(this, PARENT_MEDIA_ID); //todo move to oncreate
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }
        if (getSupportMediaController() != null) {
            getSupportMediaController().unregisterCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        CharSequence name = description.getTitle();
        CharSequence category = description.getSubtitle();

        mLine1Playing.setText(name);
        mLine2Playing.setText(category);
        Log.d(TAG, "updateMediaDescription called with name " + name + " and category " + category);
    }

    private void updateTrackPreview(MediaDescriptionCompat description) {
        CharSequence name = description.getTitle();
        CharSequence category = description.getSubtitle();

        mLine1Waiting.setText(name);
        mLine2Waiting.setText(category);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "updateDuration called ");
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mEnd.setText(DateUtils.formatElapsedTime(duration / 1000));
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;
        mLine3Playing.setText("");

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mLoading.setVisibility(INVISIBLE);
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPauseDrawable);
                mControllers.setVisibility(VISIBLE);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mControllers.setVisibility(VISIBLE);
                mLoading.setVisibility(INVISIBLE);
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mLoading.setVisibility(INVISIBLE);
                mPlayPause.setVisibility(VISIBLE);
                mPlayPause.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mPlayPause.setVisibility(INVISIBLE);
                mLoading.setVisibility(VISIBLE);
                mLine3Playing.setText(R.string.loading);
                stopSeekbarUpdate();
                break;
            default:
                Log.d(TAG, "Unhandled state " + state.getState());
        }

        mSkipNext.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) == 0
                ? INVISIBLE : VISIBLE);
        mSkipPrev.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) == 0
                ? INVISIBLE : VISIBLE);
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mSeekbar.setProgress((int) currentPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState");
        mCurrentPageNumber = mPager.getCurrentItem();
        savedInstanceState.putInt(PAGE_NUMBER_KEY, mCurrentPageNumber);
        savedInstanceState.putBoolean(PLAYER_WAS_OPENED, playerWasOpened);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {

        mCurrentPageNumber = mPager.getCurrentItem();
        if (mCurrentPageNumber > 0) {

            mPager.setCurrentItem(--mCurrentPageNumber);

        } else {

            ExitDialogFragment exitDialogFragment =
                    ExitDialogFragment.newInstance(R.string.exit_dialog_title);
            exitDialogFragment.show(getFragmentManager(), "dialog");
        }
    }

    /**
     * @param savedInstanceState
     */
    private void applySavedStates(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //screen rotated
            mCurrentPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
            if (!playerWasOpened) {
                playerWasOpened = savedInstanceState.getBoolean(PLAYER_WAS_OPENED);
            }
            if (playerWasOpened) {
                makePlayerOpen();
            }
        } else {
            // Only update from the intent if we are not recreating from a config change:
            updateFromParams(getIntent());
        }
    }

    private void updateFromParams(Intent intent) {
        if (intent != null) {
            MediaDescriptionCompat description = intent.getParcelableExtra(
                    MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            int pageNumber = intent.getIntExtra(MainActivity.EXTRA_CURRENT_PAGE_NUMBER, 0);
            if (description != null) {
                updateMediaDescription(description);
                makePlayerOpen();
                mPager.setCurrentItem(pageNumber);
            }
        }
    }

    /**
     * set UI for player as open without opening animation
     */
    private void makePlayerOpen() {
        fab.setVisibility(View.GONE);
        View music_playing = findViewById(R.id.music_playing);
        music_playing.setVisibility(VISIBLE);

        View player_waiting = findViewById(R.id.player_waiting);
        player_waiting.setVisibility(INVISIBLE);
    }


    /**
     * set UI for player as closed
     */
    private void makePlayerClosed() {
        fab.setVisibility(VISIBLE);
        View music_playing = findViewById(R.id.music_playing);
        music_playing.setVisibility(INVISIBLE);

        View player_waiting = findViewById(R.id.player_waiting);
        player_waiting.setVisibility(VISIBLE);

        fab.setViewAppears(findViewById(R.id.music_playing));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerWasOpened = true;
                fab.animateButton();
                int current = mPager.getCurrentItem();
                try {
                    onPlayMediaItemCalled(mediaBrowserManager.getMediaItems().get(current));
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "Media items are not ready. " + e.getMessage()); //todo error here
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return mMediaBrowser;
    }

    //called by pager
    @Override
    public void onMediaItemShowed(int itemPosition) {
        MediaBrowserCompat.MediaItem item = null;
        try {
            item = mediaBrowserManager.getMediaItems().get(itemPosition);
        } catch (Exception e) {
            Log.d(TAG, "item not ready");
        }
        if (item != null) {
            updateTrackPreview(item.getDescription());
//            updatePager(item.getDescription().getMediaId());//todo
        }
    }

//    //todo remove
//    private void updatePager(String mediaId) {
//        MediaControllerCompat controller = getSupportMediaController();
//
//        List<MediaSessionCompat.QueueItem> queueItemList = controller.getQueue();
//        for (int i = 0; i < queueItemList.size(); i++) {
//            if (queueItemList.get(i).getDescription().getMediaId().equals(mediaId)) {
//                mPager.setCurrentItem(i);
//                Log.d(TAG, "Pager number" + i + "setted as current");
//                break;
//            }
//        }
//    }

    @Override
    public void onPlayMediaItemCalled(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onPlayMediaItemCalled, mediaId=" + item.getMediaId());
        if (item.isPlayable()) {
            getSupportMediaController().getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        } else if (item.isBrowsable()) {
            Log.w(TAG, "onPlayMediaItemCalled on not playable item");
        } else {
            Log.w(TAG, "Ignoring MediaItem that is neither browsable nor playable: " +
                    "mediaId=" + item.getMediaId());
        }
    }

    @Override
    public void setToolbarTitle(CharSequence title) {
        Log.d(TAG, "Setting toolbar title to " + title);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
    }

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    public final String TAG = LogHelper.makeLogTag(FragmentPagerAdapter.class);

    public FragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            Log.d(TAG, "getItem called with position " + position);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return FragmentFactory.newInstance(position);
    }
}

private class OnPageChangeListenerImpl implements ViewPager.OnPageChangeListener {
    public final String TAG = LogHelper.makeLogTag(OnPageChangeListenerImpl.class);

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        Log.d(TAG, "page selected " + position);
        onMediaItemShowed(position);
        mCurrentPageNumber = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
}