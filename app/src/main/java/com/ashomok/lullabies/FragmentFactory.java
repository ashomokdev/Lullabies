package com.ashomok.lullabies;

import android.app.Fragment;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentFactory {

    static Fragment newInstance(int page) {

        Fragment fragment = null;

        switch (page) {
            case 0:
                fragment = new InsideRoom_1_Fragment();
                break;
            case 1:
                fragment = new InsideRoom_1_Fragment();
                break;
            default:
                throw new IndexOutOfBoundsException("Unexpected page number obtained");
        }


        return fragment;
    }


}
