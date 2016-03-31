package com.ashomok.lullabies;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public abstract class InsideRoomFragment extends Fragment implements View.OnTouchListener {

    private final static String TAG = "InsideRoomFragment";

    private boolean isDarkMode;

    /**
     * Respond to the user touching the screen.
     * Change images to make things appear and disappear from the screen.
     */
    @Override
    public abstract boolean onTouch(View v, MotionEvent ev);


    /**
     * Get the color from the hotspot image at point x-y.
     */
    public int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) getActivity().findViewById(hotspotId);
        if (img == null) {
            Log.d(TAG, "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d(TAG, "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }
}
