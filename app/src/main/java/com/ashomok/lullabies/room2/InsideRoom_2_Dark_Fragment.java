package com.ashomok.lullabies.room2;

import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_2_Dark_Fragment extends InsideRoomFragment {

    private static final String TAG = InsideRoom_2_Dark_Fragment.class.getSimpleName();
    private ImageButton btnCar;

    private ImageButton btnBear;

    private ImageButton btnBricks;

    private ImageButton btnPyramid;

    private ImageButton btnBall;

    private Map<ImageButton, Integer> mapButtonMusics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inside_room_2_dark, container, false);
        loadBitmapAsync(R.drawable.background2_dark, (ImageView) view.findViewById(R.id.image_background_dark));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        });

        btnBall = (ImageButton) view.findViewById(R.id.ball_btn);
        btnCar = (ImageButton) view.findViewById(R.id.car_btn);
        btnBear = (ImageButton) view.findViewById(R.id.bear_btn);
        btnBricks = (ImageButton) view.findViewById(R.id.bricks_btn);
        btnPyramid = (ImageButton) view.findViewById(R.id.pyramid_btn);

        mapButtonMusics = new HashMap<>();
        mapButtonMusics.put(btnBall, R.raw.track2_1);
        mapButtonMusics.put(btnCar, R.raw.track2_2);
        mapButtonMusics.put(btnBear, R.raw.track2_3);
        mapButtonMusics.put(btnBricks, R.raw.track2_4);
        mapButtonMusics.put(btnPyramid, R.raw.track2_5);

        for (final ImageButton btn : mapButtonMusics.keySet()) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 stopMusic();

                    mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), mapButtonMusics.get(btn));
                    mediaPlayer.start();
                }
            });
        }
        Log.d(TAG, "View created");
        return view;

    }


    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame_2, new InsideRoom_2_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
