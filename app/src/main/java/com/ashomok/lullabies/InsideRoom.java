package com.ashomok.lullabies;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class InsideRoom extends AppCompatActivity implements View.OnTouchListener {

    private final static String TAG = "InsideRoom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inside_room);

        ImageView iv = (ImageView) findViewById (R.id.image);
        if (iv != null) {
            iv.setOnTouchListener (this);
        }
    }


    /**
     * Respond to the user touching the screen.
     * Change images to make things appear and disappear from the screen.
     */
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

                if (ct.closeMatch(getResources().getColor(R.color.tag_blue), touchColor, tolerance))
                {
                    Toast.makeText(this, "tag_blue", Toast.LENGTH_SHORT).show();
                }
                else if (ct.closeMatch(getResources().getColor(R.color.tag_green), touchColor, tolerance))
                {
                    Toast.makeText(this, "tag_green", Toast.LENGTH_SHORT).show();
                }
                else if (ct.closeMatch(getResources().getColor(R.color.tag_yellow), touchColor, tolerance))
                {
                    Toast.makeText(this, "tag_yellow", Toast.LENGTH_SHORT).show();
                }
                else if (ct.closeMatch(getResources().getColor(R.color.tag_deep_blue), touchColor, tolerance))
                {
                    Toast.makeText(this, "tag_deep_blue", Toast.LENGTH_SHORT).show();
                }
                else if (ct.closeMatch(getResources().getColor(R.color.tag_red), touchColor, tolerance))
                {
                    Toast.makeText(this, "tag_red", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                return false;

        }
        return true;
    }

    /**
     * Get the color from the hotspot image at point x-y.
     */
    public int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
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
