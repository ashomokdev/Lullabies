package com.ashomok.lullabies.room2;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_2_Fragment extends InsideRoomFragment {

    private static final String TAG = InsideRoom_2_Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.inside_room_2, container, false);

        loadBitmapAsync(R.drawable.background2, (ImageView) view.findViewById(R.id.image_background));

        Log.d(TAG, "View created");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadOnLightSwitchAnimation();
    }

    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame_2, new InsideRoom_2_Dark_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
