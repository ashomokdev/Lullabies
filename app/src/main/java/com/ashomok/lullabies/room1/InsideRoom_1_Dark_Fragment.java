package com.ashomok.lullabies.room1;

import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.tools.ColorTool;
import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iuliia on 06.04.2016.
 */
public final class InsideRoom_1_Dark_Fragment extends InsideRoomFragment implements View.OnTouchListener {

    private static final String TAG = InsideRoom_1_Dark_Fragment.class.getSimpleName();

    private ImageView imageButtonLayer;

    private ImageView background;


    private enum BtnsName {blanket, pillow, lamp, picture_small, picture_big}

    private Map<BtnsName, Integer> mapButtonLayers;
    private Map<BtnsName, Integer> mapButtonMusics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inside_room_1_dark, container, false);

        imageButtonLayer = (ImageView) view.findViewById(R.id.image_layer_pressed);

        background = (ImageView) view.findViewById(R.id.image_background_dark);
        loadBitmapAsync(R.drawable.background1_dark, (ImageView) view.findViewById(R.id.image_background_dark));

        loadBitmapAsync(R.drawable.background1_matrix, (ImageView) view.findViewById(R.id.image_areas));

        mapButtonLayers = new HashMap<>();
        mapButtonMusics = new HashMap<>();

        mapButtonLayers.put(BtnsName.blanket, R.drawable.background1_layer_blanket);
        mapButtonLayers.put(BtnsName.lamp, R.drawable.background1_layer_lamp);
        mapButtonLayers.put(BtnsName.picture_big, R.drawable.background1_layer_picture_big);
        mapButtonLayers.put(BtnsName.picture_small, R.drawable.background1_layer_picture_small);
        mapButtonLayers.put(BtnsName.pillow, R.drawable.background1_layer_pillow);

        mapButtonMusics.put(BtnsName.pillow, R.raw.track1_1);
        mapButtonMusics.put(BtnsName.blanket, R.raw.track1_2);
        mapButtonMusics.put(BtnsName.lamp, R.raw.track1_3);
        mapButtonMusics.put(BtnsName.picture_big, R.raw.track1_4);
        mapButtonMusics.put(BtnsName.picture_small, R.raw.track1_5);

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

        if (ct.closeMatch(getResources().getColor(R.color.tag_sky_blue), touchColor, tolerance)) {
            setListener(BtnsName.pillow);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_green), touchColor, tolerance)) {
            setListener(BtnsName.picture_big);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_yellow), touchColor, tolerance)) {
            setListener(BtnsName.lamp);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_deep_blue), touchColor, tolerance)) {
            setListener(BtnsName.blanket);
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_red), touchColor, tolerance)) {
            setListener(BtnsName.picture_small);
        }
        else {
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

    private void setBtnMusic(final BtnsName name) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), mapButtonMusics.get(name));
        mediaPlayer.start();
    }


    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame_1, new InsideRoom_1_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        trans.commit();
    }

    private void setBtnResource(int resource_id) {

        try {
            loadBitmapAsync(resource_id, imageButtonLayer);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
