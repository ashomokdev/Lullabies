package com.ashomok.lullabies;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.support.v7.app.AppCompatDelegate;
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
import com.ashomok.lullabies.services.MediaPlayerServiceTools;
import com.ashomok.lullabies.tools.CircleView;
import com.ashomok.lullabies.tools.FABReval;
import com.ashomok.lullabies.tools.LogHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class MainActivity extends AppCompatActivity implements MediaPlayerServiceTools.MediaPlayerCallback {

    //todo remove this
    //seems not safe to use
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected static final int NUM_ITEMS = FragmentFactory.trackDataList.size();

    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;
    private TextView mLine1;
    private TextView mLine2;
    private TextView mLine3;
    private ProgressBar mLoading;
    private View mControllers;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private ViewPager mPager;
    private FABReval fab;

    public static final String PAGE_NUMBER_KEY = "page_number";
    public static final String IS_PLAYING_KEY = "is_playing";

    private boolean isPlaying;
    private int currentPageNumber;

    private AdContainer adContainer;
    private FirebaseAnalytics mFirebaseAnalytics;

    private String mCurrentArtUrl;
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

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            LogHelper.d(TAG, "onPlaybackstate changed", state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogHelper.d(TAG, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
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


            //// TODO: 2/27/17  
//            mBackgroundImage = (ImageView) findViewById(R.id.background_image);
            mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);
            mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
            mPlayPause = (ImageView) findViewById(R.id.play_pause);
            mSkipNext = (ImageView) findViewById(R.id.next);
            mSkipPrev = (ImageView) findViewById(R.id.prev);
            mStart = (TextView) findViewById(R.id.startText);
            mEnd = (TextView) findViewById(R.id.endText);
            mSeekbar = (SeekBar) findViewById(R.id.seekBar1);
            mLine1 = (TextView) findViewById(R.id.line1);
            mLine2 = (TextView) findViewById(R.id.line2);
            mLine3 = (TextView) findViewById(R.id.line3);
            mLoading = (ProgressBar) findViewById(R.id.progressBar1);
            mControllers = findViewById(R.id.controllers);
            fab = (FABReval) findViewById(R.id.fab);
            fab.setViewAppears(findViewById(R.id.music_playing));

            mSkipNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaControllerCompat.TransportControls controls =
                            getSupportMediaController().getTransportControls();
                    controls.skipToNext();
                }
            });

            mSkipPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaControllerCompat.TransportControls controls =
                            getSupportMediaController().getTransportControls();
                    controls.skipToPrevious();
                }
            });

            mPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaybackStateCompat state = getSupportMediaController().getPlaybackState();
                    if (state != null) {
                        MediaControllerCompat.TransportControls controls =
                                getSupportMediaController().getTransportControls();
                        switch (state.getState()) {
                            case PlaybackStateCompat.STATE_PLAYING: // fall through
                            case PlaybackStateCompat.STATE_BUFFERING:
                                controls.pause();
                                stopSeekbarUpdate();
                                break;
                            case PlaybackStateCompat.STATE_PAUSED:
                            case PlaybackStateCompat.STATE_STOPPED:
                                controls.play();
                                scheduleSeekbarUpdate();
                                break;
                            default:
                                LogHelper.d(TAG, "onClick with state ", state.getState());
                        }
                    }
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


            //todo reduntant?
//            // Only update from the intent if we are not recreating from a config change:
//            if (savedInstanceState == null) {
//                updateFromParams(getIntent());
//            }


            //// TODO: 2/27/17  
//            mMediaBrowser = new MediaBrowserCompat(this,
//                    new ComponentName(this, MusicService.class), mConnectionCallback, null);


            currentPageNumber = 0;
            isPlaying = false;

            //screen rotated
            if (savedInstanceState != null) {
                currentPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
                isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY);
            }

            ObtainSavedStates();

            FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getFragmentManager());
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(currentPageNumber);
            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

            CircleView circleView = (CircleView) findViewById(R.id.circle_view);
            circleView.setColorAccent(getResources().getColor(R.color.colorAccent));
            circleView.setColorBase(getResources().getColor(R.color.colorPrimary));
            circleView.setViewPager(mPager);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * make music_playing and music_not_playing have the same height
     */
    private void resizeBottomLayout() {
        final View musicPlaying = findViewById(R.id.music_playing);
        musicPlaying.post(new Runnable() {
            @Override
            public void run() {
                final int height = musicPlaying.getHeight();

                final View musicNotPlaying = findViewById(R.id.music_not_playing);
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

    //todo remove - redundant
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
        MediaControllerCompat mediaController = new MediaControllerCompat(
                MainActivity.this, token);
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mCallback);
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
            mMediaBrowser.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }
        if (getSupportMediaController() != null) {
            getSupportMediaController().unregisterCallback(mCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

//    private void fetchImageAsync(@NonNull MediaDescriptionCompat description) {
//        if (description.getIconUri() == null) {
//            return;
//        }
//        String artUrl = description.getIconUri().toString();
//        mCurrentArtUrl = artUrl;
//        AlbumArtCache cache = AlbumArtCache.getInstance();
//        Bitmap art = cache.getBigImage(artUrl);
//        if (art == null) {
//            art = description.getIconBitmap();
//        }
//        if (art != null) {
//            // if we have the art cached or from the MediaDescription, use it:
//            mBackgroundImage.setImageBitmap(art);
//        } else {
//            // otherwise, fetch a high res version and update:
//            cache.fetch(artUrl, new AlbumArtCache.FetchListener() {
//                @Override
//                public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
//                    // sanity check, in case a new fetch request has been done while
//                    // the previous hasn't yet returned:
//                    if (artUrl.equals(mCurrentArtUrl)) {
//                        mBackgroundImage.setImageBitmap(bitmap);
//                    }
//                }
//            });
//        }
//    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        LogHelper.d(TAG, "updateMediaDescription called ");
        mLine1.setText(description.getTitle());
        mLine2.setText(description.getSubtitle());
//        fetchImageAsync(description);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        LogHelper.d(TAG, "updateDuration called ");
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mEnd.setText(DateUtils.formatElapsedTime(duration / 1000));
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;

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
                mLine3.setText(R.string.loading);
                stopSeekbarUpdate();
                break;
            default:
                LogHelper.d(TAG, "Unhandled state ", state.getState());
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


    //todo reduntant?
    //todo change architecture. Make service know the plase music pause.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        currentPageNumber = mPager.getCurrentItem();
        savedInstanceState.putInt(PAGE_NUMBER_KEY, currentPageNumber);
        savedInstanceState.putBoolean(IS_PLAYING_KEY, isPlaying);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {

        int currentPageNumber = mPager.getCurrentItem();
        if (currentPageNumber > 0) {

            mPager.setCurrentItem(--currentPageNumber);

        } else {

            ExitDialogFragment exitDialogFragment = ExitDialogFragment.newInstance(R.string.exit_dialog_title);

            exitDialogFragment.show(getFragmentManager(), "dialog");
        }

    }


    /**
     * When create Activity after back action in notification - try to obtain previous activity states
     */
    private void ObtainSavedStates() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentPageNumber = extras.getInt(PAGE_NUMBER_KEY);
            try {
                isPlaying = extras.getBoolean(IS_PLAYING_KEY);
            } catch (Exception e) {
                isPlaying = false;
            }
        }
    }

    /**
     * playing music track finished
     */
    @Override
    public void onCompletion() {

        currentPageNumber++;
        if (currentPageNumber >= mPager.getAdapter().getCount()) {
            currentPageNumber = 0;
        }

        mPager.setCurrentItem(currentPageNumber);
    }


    private class OnPageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        //todo update bottom layout here
        @Override
        public void onPageSelected(final int position) {

            currentPageNumber = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * Created by iuliia on 2/6/17.
     */
    public static class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        public FragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.newInstance(position);
        }
    }
}