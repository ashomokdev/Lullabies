package com.ashomok.lullabies.room1;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;


/**
 * Created by Iuliia on 31.03.2016.
 */
public final class InsideRoom_1_Fragment extends InsideRoomFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.inside_room_1, container, false);

        return view;
    }

    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame, new InsideRoom_1_Dark_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }
}
