package com.ashomok.lullabies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Iuliia on 31.03.2016.
 */
public class InsideRoom_1_Fragment extends InsideRoomFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_inside_room, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.image);
        if (iv != null) {
            iv.setOnTouchListener(this);
        }
        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                // On the UP, we do the click action.
                // The hidden image (image_areas) has three different hotspots on it.
                // The colors are red, blue, and yellow.
                // Use image_areas to determine which region the user touched.
                int touchColor = getHotspotColor(R.id.image_areas, evX, evY);

                // Compare the touchColor to the expected values. Switch to a different image, depending on what color was touched.
                // Note that we use a Color Tool object to test whether the observed color is close enough to the real color to
                // count as a match. We do this because colors on the screen do not match the map exactly because of scaling and
                // varying pixel density.
                ColorTool ct = new ColorTool();
                int tolerance = 15;

                if (ct.closeMatch(getResources().getColor(R.color.tag_blue), touchColor, tolerance)) {
                    Toast.makeText(getActivity(), "tag_blue", Toast.LENGTH_SHORT).show();
                } else if (ct.closeMatch(getResources().getColor(R.color.tag_green), touchColor, tolerance)) {
                    Toast.makeText(getActivity(), "tag_green", Toast.LENGTH_SHORT).show();
                } else if (ct.closeMatch(getResources().getColor(R.color.tag_yellow), touchColor, tolerance)) {
                    Toast.makeText(getActivity(), "tag_yellow", Toast.LENGTH_SHORT).show();
                } else if (ct.closeMatch(getResources().getColor(R.color.tag_deep_blue), touchColor, tolerance)) {
                    Toast.makeText(getActivity(), "tag_deep_blue", Toast.LENGTH_SHORT).show();
                } else if (ct.closeMatch(getResources().getColor(R.color.tag_red), touchColor, tolerance)) {
                    Toast.makeText(getActivity(), "tag_red", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                return false;

        }
        return true;
    }
}
