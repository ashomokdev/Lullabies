package com.ashomok.lullabies;

import android.app.Fragment;
/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentFactory {

    public static Fragment newInstance(int position) {

        Fragment fragment = MusicFragment.newInstance(position);
        return fragment;
    }
}
