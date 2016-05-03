package com.ashomok.lullabies;

import android.app.Fragment;

import com.ashomok.lullabies.room1.InsideRoom_1_Root;
import com.ashomok.lullabies.room2.InsideRoom_2_Root;
import com.ashomok.lullabies.room3.InsideRoom_3_Root;

/**
 * Created by Iuliia on 31.03.2016.
 */
class FragmentFactory {

    static Fragment newInstance(int page) {

//TODO add background color, music, image to constructor
        Fragment fragment = MusicFragment.newInstance(page);



        return fragment;
    }
}
