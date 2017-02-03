package com.ashomok.lullabies.ads;

import android.app.Activity;
import android.util.Log;

import com.ashomok.lullabies.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by iuliia on 7/26/16.
 */
public class AdMobContainerImpl implements AdContainer {


    public static final String appID = "ca-app-pub-5221781428763224~4451105598";

    private static final String TAG = AdMobContainerImpl.class.getSimpleName();
    private final Activity context;

    public AdMobContainerImpl(Activity context) {
        this.context = context;

    }

    @Override
    public void initAd(boolean isAdActive) {
        if (isAdActive) {
            if (context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                //init banner

                MobileAds.initialize(context.getApplicationContext(), appID);
                AdView mAdView = (AdView) context.findViewById(R.id.adBannerView);

                AdRequest adRequest;

                adRequest = new AdRequest.Builder().build();

                mAdView.loadAd(adRequest);
            }
        }
    }

    @Override
    public void init() {
        initAd(com.ashomok.lullabies.Settings.isAdActive);
    }

}
