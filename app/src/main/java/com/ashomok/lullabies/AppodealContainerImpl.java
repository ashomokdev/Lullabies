package com.ashomok.lullabies;

import android.app.Activity;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.util.Log;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
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
            Appodeal.setTesting(Settings.isAdinTestMode);
            Appodeal.setLogging(Settings.isAdinTestMode);

            Appodeal.disableLocationPermissionCheck();
            Appodeal.initialize(context, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER);

            tryToShowBanner();

            Appodeal.setBannerCallbacks(new BannerCallbacks() {

                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {

                    tryToShowBanner();
                }

                @Override
                public void onBannerFailedToLoad() {
                    Log.e(TAG, "onBannerFailedToLoad");
                }

                @Override
                public void onBannerShown() {
                }

                @Override
                public void onBannerClicked() {

                }
            });
        }
    }

    private void tryToShowBanner() {
        if (Appodeal.isLoaded(Appodeal.BANNER_BOTTOM)) {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Appodeal.show(context, Appodeal.BANNER_BOTTOM);
            } else {
                Appodeal.hide(context, Appodeal.BANNER_BOTTOM);
            }
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

        if (Settings.isAdActive) {
            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                Appodeal.show(context, Appodeal.INTERSTITIAL);
            }
        }
    }
}
