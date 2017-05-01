package com.ashomok.lullabies.services.playback;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.lullabies.MainActivity;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.tools.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ashomok.lullabies.tools.LogHelper.printList;

/**
 * Created by iuliia on 3/30/17.
 */

public class MediaBrowserManager {

    private static final String TAG = LogHelper.makeLogTag(MediaBrowserManager.class);
    private final Activity activity;
    private String mMediaId;
    private MediaListener mMediaListener;
    private View mErrorView;
    private TextView mErrorMessage;
    private ViewPager mPager;

    public ArrayList<MediaBrowserCompat.MediaItem> getMediaItems() {
        return mediaItems;
    }

    ArrayList<MediaBrowserCompat.MediaItem> mediaItems;

    public MediaBrowserManager(Activity activity, String parentMediaId) {
        this.activity = activity;
        mMediaId = parentMediaId;
        mErrorView = activity.findViewById(R.id.playback_error);
        mErrorMessage = (TextView) mErrorView.findViewById(R.id.error_message);
        mediaItems = new ArrayList<>();
        mMediaListener = (MediaListener) activity;
        mPager = (ViewPager) activity.findViewById(R.id.pager);
    }

    //todo use it
    public void release() {
        MediaBrowserCompat mediaBrowser = mMediaListener.getMediaBrowser();
        if (mediaBrowser != null && mediaBrowser.isConnected() && mMediaId != null) {
            mediaBrowser.unsubscribe(mMediaId);
        }
        MediaControllerCompat controller = ((FragmentActivity) activity).getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
        mMediaListener = null;
    }

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        Log.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        checkForUserVisibleErrors(children.isEmpty());
                        printList(TAG + "onChildrenLoaded", children);
                        mediaItems.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            mediaItems.add(item);
                        }

                        //set first item as active
                        mMediaListener.onMediaItemShowed(0); //update preview for first

                    } catch (Throwable t) {
                        Log.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Log.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(activity, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                    checkForUserVisibleErrors(true);
                }
            };

    // Called when the MediaBrowser is connected. This method is either called by the activity
    // where the connection completes
    public void onConnected() {

        if (mMediaId == null) {
            mMediaId = mMediaListener.getMediaBrowser().getRoot(); //set __BY_GENRE__/Lullabies instead of root
        }
        updateTitle();

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unsubscribe first.
        mMediaListener.getMediaBrowser().unsubscribe(mMediaId);

        mMediaListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = ((FragmentActivity) activity)
                .getSupportMediaController();
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    private void updateTitle() {
        if (MediaIDHelper.MEDIA_ID_ROOT.equals(mMediaId)) {
            mMediaListener.setToolbarTitle(null);
            return;
        }

        MediaBrowserCompat mediaBrowser = mMediaListener.getMediaBrowser();
        mediaBrowser.getItem(mMediaId, new MediaBrowserCompat.ItemCallback() {
            @Override
            public void onItemLoaded(MediaBrowserCompat.MediaItem item) {
                mMediaListener.setToolbarTitle(
                        item.getDescription().getTitle());
            }
        });
    }

    private void checkForUserVisibleErrors(boolean forceError) {
        boolean showError = forceError;
        //if state is ERROR and metadata!=null, use playback state error message:
        MediaControllerCompat controller = ((FragmentActivity) activity).getSupportMediaController();
        if (controller != null
                && controller.getMetadata() != null
                && controller.getPlaybackState() != null
                && controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_ERROR
                && controller.getPlaybackState().getErrorMessage() != null) {
            mErrorMessage.setText(controller.getPlaybackState().getErrorMessage());
            showError = true;
        } else if (forceError) {
            // Finally, if the caller requested to show error, show a generic message:
            mErrorMessage.setText(R.string.error_loading_media);
            showError = true;
        }

        mErrorView.setVisibility(showError ? View.VISIBLE : View.GONE);
        Log.d(TAG, "checkForUserVisibleErrors. forceError=" + forceError +
                " showError=" + showError);
    }

    // Receive callbacks from the MediaController. Here we update our pager.
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    if (metadata == null) {
                        return;
                    }
                    String mediaId = metadata.getDescription().getMediaId();
                    Log.d(TAG, "Received metadata change to media " +
                            mediaId);

                    int position = getPositionInPlayingQueue(mediaId);
                    ((MainActivity) activity).mCurrentPageNumber = position;

                    mPager.setCurrentItem(position); //todo mPager.setCurrentItem calls too many times - refactoring needed
                    Log.d(TAG, "pager go to " + position + " position");
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    Log.d(TAG, "Received state change: " + state);
                    checkForUserVisibleErrors(false);
                }
            };

    public int getPositionInPlayingQueue(String mediaId) {
        int position = 0;
        if (mPager.getAdapter().getCount() != mediaItems.size()) {
            Log.e(TAG, "Pager size and music array have different sizes. " +
                    "mPager.getAdapter().getCount() = " + mPager.getAdapter().getCount() +
                    "mediaItems.size()" + mediaItems.size());
        } else {
            for (int i = 0; i < mediaItems.size(); i++) {
                Log.d(TAG, mediaItems.get(i).getDescription().getMediaId());

                if (mediaItems.get(i).getDescription().getMediaId().contains(mediaId)) {
                    position = i;
                }
            }
        }
        return position;
    }

    public interface MediaListener extends MediaBrowserProvider {
        void onMediaItemShowed(int itemPosition);

        void onPlayMediaItemCalled(MediaBrowserCompat.MediaItem item);

        void setToolbarTitle(CharSequence title);
    }
}
