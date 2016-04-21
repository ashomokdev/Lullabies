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

        Fragment fragment = null;

        switch (page) {
            case 0:
                fragment = new InsideRoom_1_Root();
                break;

            case 1:
                fragment = new InsideRoom_2_Root();
                break;

            case 2:
                fragment = new InsideRoom_3_Root();
                break;


            default:
                throw new IndexOutOfBoundsException("Unexpected page number obtained");
        }


        return fragment;
    }
}
