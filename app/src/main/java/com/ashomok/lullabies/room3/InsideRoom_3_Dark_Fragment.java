package com.ashomok.lullabies.room3;

import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;
import com.ashomok.lullabies.tools.ColorTool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_3_Dark_Fragment extends InsideRoomFragment implements View.OnTouchListener {
    private static final String TAG = InsideRoom_3_Dark_Fragment.class.getSimpleName();

    private ImageButton btnPyramid;

    private ImageButton btnBeanbag;

    private ImageView imageButtonLayer;

    private Map<ImageButton, Integer> mapButtonMusics;

    private enum BtnsName {chicken, ant, monkey}

    private Map<BtnsName, Integer> mapButtonLayers;
    private Map<BtnsName, Integer> mapButtonLayerMusics;

    private ImageView background;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inside_room_3_dark, container, false);
        imageButtonLayer = (ImageView) view.findViewById(R.id.image_layer_pressed);

        background = (ImageView) view.findViewById(R.id.image_background_dark);

        loadBitmapAsync(R.drawable.background3_dark, (ImageView) view.findViewById(R.id.image_background_dark));

        loadBitmapAsync(R.drawable.background3_matrix, (ImageView) view.findViewById(R.id.image_areas));

        btnBeanbag = (ImageButton) view.findViewById(R.id.beanbag_btn);
        btnPyramid = (ImageButton) view.findViewById(R.id.pyramid_btn);


        mapButtonMusics = new HashMap<>();
        mapButtonMusics.put(btnBeanbag, R.raw.track3_4);
        mapButtonMusics.put(btnPyramid, R.raw.track3_5);

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

        mapButtonLayers = new HashMap<>();
        mapButtonLayerMusics = new HashMap<>();

        mapButtonLayers.put(BtnsName.chicken, R.drawable.background3_layer_chicken);
        mapButtonLayers.put(BtnsName.ant, R.drawable.background3_layer_ant);
        mapButtonLayers.put(BtnsName.monkey, R.drawable.background3_layer_monkey);

        mapButtonLayerMusics.put(BtnsName.chicken, R.raw.track3_1);
        mapButtonLayerMusics.put(BtnsName.ant, R.raw.track3_2);
        mapButtonLayerMusics.put(BtnsName.monkey, R.raw.track3_3);

        Log.d(TAG, "View created");
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        if (background != null) {
            background.setOnTouchListener(this);
        }
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

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        try {
            final int action = ev.getAction();

            final int evX = (int) ev.getX();
            final int evY = (int) ev.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // On the UP, we do the click action.
                    // The hidden image (image_areas) has three different hotspots on it.
                    // The colors are red, blue, and yellow.
                    // Use image_areas to determine which region the user touched.
                    int touchColor = getHotspotColor(R.id.image_areas, evX, evY);

                    setImageBtnListener(touchColor);
                    break;

                case MotionEvent.ACTION_UP:
                    break;

                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return true;
    }


    private void setImageBtnListener(int touchColor) throws Exception {
        // Compare the touchColor to the expected values. Switch to a different image, depending on what color was touched.
        // Note that we use a Color Tool object to test whether the observed color is close enough to the real color to
        // count as a match. We do this because colors on the screen do not match the map exactly because of scaling and
        // varying pixel density.
        ColorTool ct = new ColorTool();
        int tolerance = 15;

        if (ct.closeMatch(getResources().getColor(R.color.tag_green), touchColor, tolerance)) {
            setListener(BtnsName.ant);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_yellow), touchColor, tolerance)) {
            setListener(BtnsName.monkey);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_red), touchColor, tolerance)) {
            setListener(BtnsName.chicken);
        } else {
            imageButtonLayer.setVisibility(View.INVISIBLE);
            stopMusic();
        }
    }

    private void setListener(BtnsName name) {
        try {
            setBtnResource(mapButtonLayers.get(name));
            setBtnMusic(name);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void setBtnResource(int resource_id) {

        try {
            loadBitmapAsync(resource_id, imageButtonLayer);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void setBtnMusic(final BtnsName name) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), mapButtonLayerMusics.get(name));
        mediaPlayer.start();
    }
}
