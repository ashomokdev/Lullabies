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

import com.ashomok.lullabies.ColorTool;
import com.ashomok.lullabies.InsideRoomFragment;
import com.ashomok.lullabies.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iuliia on 06.04.2016.
 */
public final class InsideRoom_1_Dark_Fragment extends InsideRoomFragment implements View.OnTouchListener  {
    private static final String TAG = InsideRoom_1_Dark_Fragment.class.getSimpleName();

    private ImageView imageButtonLayer;

    private ImageView background;

    private MediaPlayer mediaPlayer;


    private enum BtnsName {blanket, pillow, lamp, picture_small, picture_big}

    private final Map<BtnsName, Integer> map_button_layers = new HashMap<>();
    private final Map<BtnsName, Integer> map_button_musics = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inside_room_1_dark, container, false);

        imageButtonLayer = (ImageView) view.findViewById(R.id.image_layer_pressed);

        background = (ImageView) view.findViewById(R.id.image_background);
        if (background != null) {
            background.setOnTouchListener(this);
        }


        map_button_layers.put(BtnsName.blanket, R.drawable.background1_layer_blanket);
        map_button_layers.put(BtnsName.lamp, R.drawable.background1_layer_lamp);
        map_button_layers.put(BtnsName.picture_big, R.drawable.background1_layer_picture_big);
        map_button_layers.put(BtnsName.picture_small, R.drawable.background1_layer_picture_small);
        map_button_layers.put(BtnsName.pillow, R.drawable.background1_layer_pillow);

        map_button_musics.put(BtnsName.blanket, R.raw.track2);
        map_button_musics.put(BtnsName.lamp, R.raw.track3);
        map_button_musics.put(BtnsName.picture_big, R.raw.track4);
        map_button_musics.put(BtnsName.picture_small, R.raw.track5);
        map_button_musics.put(BtnsName.pillow, R.raw.track6);

        return view;
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
        } else {
            throw new Exception("Color not matched, button was not created");
        }
    }

    private void setListener(BtnsName name) {
        setBtnResource(map_button_layers.get(name));
        setBtnMusic(name);
    }

    private void setBtnMusic(final BtnsName name) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), map_button_musics.get(name));
        mediaPlayer.start();
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        stopMusic();
    }


    @Override
    protected void onLightSwitched() {
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.root_frame, new InsideRoom_1_Fragment());

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }

    private void setBtnResource(int resource_id) {
        imageButtonLayer.setImageResource(resource_id);
    }
}