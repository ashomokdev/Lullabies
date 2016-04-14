package com.ashomok.lullabies.room3;

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
public class InsideRoom_3_Dark_Fragment extends InsideRoomFragment {
    private static final String TAG = InsideRoom_3_Dark_Fragment.class.getSimpleName();

    private ImageButton btnPyramid;

    private ImageButton btnBeanbag;

    private Map<ImageButton, Integer> mapButtonMusics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inside_room_3_dark, container, false);
        loadBitmapAsync(R.drawable.background3_dark, (ImageView) view.findViewById(R.id.image_background_dark));

        loadBitmapAsync(R.drawable.background3_matrix, (ImageView) view.findViewById(R.id.image_areas));

        btnBeanbag = (ImageButton) view.findViewById(R.id.beanbag_btn);
        btnPyramid = (ImageButton) view.findViewById(R.id.pyramid_btn);


        mapButtonMusics = new HashMap<>();
        mapButtonMusics.put(btnBeanbag, R.raw.track2_1);
        mapButtonMusics.put(btnPyramid, R.raw.track2_2);


        for (final ImageButton btn : mapButtonMusics.keySet()) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

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

        trans.replace(R.id.root_frame_3, new InsideRoom_3_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
