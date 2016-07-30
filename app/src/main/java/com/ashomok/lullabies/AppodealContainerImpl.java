package com.ashomok.lullabies;

import android.app.Activity;
import android.util.Log;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.ashomok.lullabies.tools.CustomViewPager;

/**
 * Created by Iuliia on 27.07.2016.
 */


//if delete this class - clean also AndroidManifest.xml and delete Lullabies/app/libs
public class AppodealContainerImpl implements AdContainer, CustomViewPager.OnSwipeOutListener {

    private static final String appKey = "52efd60ee003eaf63d1b5391ec77886dc2b63993d5ac5e95";
    private static final String TAG = AppodealContainerImpl.class.getSimpleName();
    private final Activity context;

    public AppodealContainerImpl(Activity context) {
        this.context = context;

    }

    @Override
    public void initAd(boolean isAdActive) {

        if (isAdActive) {
            Appodeal.disableLocationPermissionCheck();
            Appodeal.initialize(context, appKey, Appodeal.INTERSTITIAL);
        }

        if (context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            //init banner
            Appodeal.initialize(context, appKey, Appodeal.BANNER);
            Appodeal.show(context, Appodeal.BANNER_BOTTOM);
        }
    }

    @Override
    public void init() {
        initAd(com.ashomok.lullabies.Settings.isAdActive);
    }

    @Override
    public void onSwipeOutAtStart() {

    }

    @Override
    public void onSwipeOutAtEnd() {
        Log.d(TAG, "onSwipeOutAtEnd()");

        if (com.ashomok.lullabies.Settings.isAdActive) {
            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                Appodeal.show(context, Appodeal.INTERSTITIAL);
            }
        }
    }
}
