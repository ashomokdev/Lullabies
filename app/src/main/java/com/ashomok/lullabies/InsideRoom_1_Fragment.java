package com.ashomok.lullabies;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class InsideRoom_1_Fragment extends InsideRoomFragment {

    private static final String TAG = "InsideRoom_1_Fragment";
    MediaPlayer introSound, bellSound;

    private ImageView imageButtonLayer;

    private enum BtnsName {blanket, pillow, lamp, picture_small, picture_big}

    private final Map<BtnsName, Integer> map_buttons = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_inside_room_1, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_background_dark);
        if (imageView != null) {
            imageView.setOnTouchListener(this);
        }

        imageButtonLayer = (ImageView) view.findViewById(R.id.image_layer_pressed);

        map_buttons.put(BtnsName.blanket, R.drawable.background1_layer_blanket);
        map_buttons.put(BtnsName.lamp, R.drawable.background1_layer_lamp);
        map_buttons.put(BtnsName.picture_big, R.drawable.background1_layer_picture_big);
        map_buttons.put(BtnsName.picture_small, R.drawable.background1_layer_picture_small);
        map_buttons.put(BtnsName.pillow, R.drawable.background1_layer_pillow);


//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            int i = 0;
//
//            public void run() {
//
//                i++;
//                if (i > mThumbIds.length - 1) {
//                    i = 0;
//                }
//                handler.postDelayed(this, 2000);  //for interval...
//            }
//        };
//        handler.postDelayed(runnable, 2000); //for initial delay..

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
        }
        catch (Exception e)
        {
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
            setBtnResource(map_buttons.get(BtnsName.pillow));
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_green), touchColor, tolerance)) {
            setBtnResource(map_buttons.get(BtnsName.picture_big));
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_yellow), touchColor, tolerance)) {
            setBtnResource(map_buttons.get(BtnsName.lamp));
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_deep_blue), touchColor, tolerance)) {
            setBtnResource(map_buttons.get(BtnsName.blanket));
        } else if (ct.closeMatch(getResources().getColor(R.color.tag_red), touchColor, tolerance)) {
            setBtnResource(map_buttons.get(BtnsName.picture_small));
        } else {
            throw new Exception("Color not matched, button was not created");
        }
    }

    private void setBtnResource(int resource_id) {
        imageButtonLayer.setImageResource(resource_id);
    }
}
