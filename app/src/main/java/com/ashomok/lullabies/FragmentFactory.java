package com.ashomok.lullabies;

import android.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentFactory {

    public static Fragment newInstance(int position) {

        Fragment fragment = MusicFragment.newInstance(position);
        return fragment;
    }
}
