package com.ashomok.lullabies.room3;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.room2.InsideRoom_2_Dark_Fragment;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_3_Fragment extends InsideRoomFragment {
    private static final String TAG = InsideRoom_3_Fragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.inside_room_3, container, false);

        loadBitmapAsync(R.drawable.background3, (ImageView) view.findViewById(R.id.image_background));

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

        trans.replace(R.id.root_frame_3, new InsideRoom_3_Dark_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
