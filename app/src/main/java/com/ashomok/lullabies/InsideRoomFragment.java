package com.ashomok.lullabies;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ashomok.lullabies.tools.BitmapWorkerTask;

//todo fix problem Grow heap (frag case) to 41.553MB for 4831188-byte allocation
public abstract class InsideRoomFragment extends Fragment {

    private final static String TAG = "InsideRoomFragment";

    protected ImageButton light_switch_btn;

    protected MediaPlayer mediaPlayer;


//    /**
//     * Get the color from the hotspot image at point x-y.
//     */
//    public int getHotspotColor(int hotspotId, int x, int y) {
//        try {
//            ImageView img = (ImageView) getActivity().findViewById(hotspotId);
//            if (img == null) {
//                Log.d(TAG, "Hot spot image not found");
//                return 0;
//            } else {
//                img.setDrawingCacheEnabled(true);
//                Bitmap hotspots = ((BitmapDrawable)img.getDrawable()).getBitmap();
//                if (hotspots == null) {
//                    Log.d(TAG, "Hot spot bitmap was not created");
//                    return 0;
//                } else {
//                    img.setDrawingCacheEnabled(false);
//                    return hotspots.getPixel(x, y);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            Log.e(TAG, e.getMessage());
//        }
//        return 0;
//    }

    /**
     * Get the color from the hotspot image at point x-y.
     */
    public int getHotspotColor(int hotspotId, int x, int y) {
        try {

            View imageView = getActivity().findViewById(hotspotId);
            if (imageView == null) {
                Log.d(TAG, "Hot spot image not found");
                return 0;
            } else {
                imageView.setDrawingCacheEnabled(true);
                imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));

                imageView.layout(0, 0,
                        imageView.getWidth(), imageView.getHeight());

                imageView.buildDrawingCache(true);
                Bitmap drawingCache = imageView.getDrawingCache();
                if (drawingCache == null) {
                    throw new NullPointerException("Drawing cache was not obtained. May be the view is too large for cache.");
                }
                Bitmap hotspots = Bitmap.createBitmap(drawingCache);
                imageView.destroyDrawingCache();
                imageView.setDrawingCacheEnabled(false);

                if (hotspots == null) {
                    Log.d(TAG, "Hot spot bitmap was not created");
                    return 0;
                } else {
                    return hotspots.getPixel(x, y);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public void loadBitmapAsync(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

//    private Bitmap loadBitmapFromView(View v) {
//        try {
//            int t = v.getLayoutParams().width;
//            Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(b);
//            v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
//            v.draw(c);
//            return b;
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        }
//        return null;
//    }

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

    public void loadOnLightSwitchAnimation() {
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

    protected void stopMusic() {
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

    protected abstract void onLightSwitched();
}
