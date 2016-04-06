package com.ashomok.lullabies;

import android.app.Fragment;
import com.ashomok.lullabies.room1.InsideRoom_1_Root;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentFactory {

    static Fragment newInstance(int page) {

        Fragment fragment = null;

        switch (page) {
            case 0:
                fragment = new InsideRoom_1_Root();

                break;
            default:
                throw new IndexOutOfBoundsException("Unexpected page number obtained");
        }


        return fragment;
    }


}
