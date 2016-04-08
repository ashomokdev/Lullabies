package com.ashomok.lullabies;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public abstract class InsideRoomFragment extends Fragment {

    private final static String TAG = "InsideRoomFragment";

    protected ImageButton light_switch_btn;


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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        light_switch_btn = (ImageButton) view.findViewById(R.id.light_switch_btn);
        light_switch_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    onLightSwitched();
                                                }
                                            }
        );
    }

    public void loadOnLightSwitchAnimation()
    {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_light_switch);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_light_switch);
                animation.setAnimationListener(this);
                light_switch_btn.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        light_switch_btn.startAnimation(animation);
    }

    protected abstract void onLightSwitched();
}
