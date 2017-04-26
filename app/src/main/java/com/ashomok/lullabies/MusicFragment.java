package com.ashomok.lullabies;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.rate_app.RateAppAsker;
import com.ashomok.lullabies.services.playback.cache.AlbumArtCache;
import com.ashomok.lullabies.settings.Settings;
import com.ashomok.lullabies.tools.LogHelper;


/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(MusicFragment.class);
    private static final String ARGUMENT_POSITION = "position";
    private int position;
    private int mCurrentDrawableID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARGUMENT_POSITION);

        RateAppAsker.init(getActivity());
    }


    static MusicFragment newInstance(int position) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();

        arguments.putInt(ARGUMENT_POSITION,position);

        pageFragment.setArguments(arguments);
        return pageFragment;
    }


    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_fragment, null);
        Settings settings = new Settings();
        fetchImageAsync(view, settings.getMusicSource().get(position).getImageDrawableId());
        return view;
    }

    private void fetchImageAsync(View parentView, int drawableID) {
        AlbumArtCache cache = AlbumArtCache.getInstance();

        if (drawableID != 0) {
            mCurrentDrawableID = drawableID;
            // async fetch the album art icon
            Bitmap art = AlbumArtCache.getInstance().getBigImage(drawableID);

            if (art == null) {
                Log.w(TAG, "bitmap == null. Image can not be set.");
            }
            final ImageView image = (ImageView) parentView.findViewById(R.id.image);
            if (art != null) {
                // if we have the art cached or from the MediaDescription, use it:
                image.setImageBitmap(art);
            } else {
                // otherwise, fetch a high res version and update:
                cache.fetch(drawableID, new AlbumArtCache.FetchListener() {
                    @Override
                    public void onFetched(int drawableId, Bitmap bitmap, Bitmap iconImage) {
                        // sanity check, in case a new fetch request has been done while
                        // the previous hasn't yet returned:
                        if (mCurrentDrawableID == drawableId) {
                            image.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }

    }

}
