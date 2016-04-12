package com.ashomok.lullabies.room2;

import android.app.FragmentTransaction;

import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_2_Dark_Fragment extends InsideRoomFragment {
    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame_2, new InsideRoom_2_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }
}
