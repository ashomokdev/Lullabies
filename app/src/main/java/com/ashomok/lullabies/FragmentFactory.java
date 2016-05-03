package com.ashomok.lullabies;

import android.app.Fragment;

/**
 * Created by Iuliia on 31.03.2016.
 */
class FragmentFactory {

    static Fragment newInstance(int page) {

        Fragment fragment = MusicFragment.newInstance(page);
        return fragment;
    }
}
