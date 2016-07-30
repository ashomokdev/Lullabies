package com.ashomok.lullabies;

import android.app.Activity;
import android.util.Log;

import com.ashomok.lullabies.tools.CustomViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by iuliia on 7/26/16.
 */
public class AdMobContainerImpl implements AdContainer, CustomViewPager.OnSwipeOutListener {


    public static final String appID = "ca-app-pub-5221781428763224~4451105598";

    private static final String TAG = AdMobContainerImpl.class.getSimpleName();
    private final Activity context;
    private InterstitialAd mInterstitialAd;

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

            //init interstitial
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }
    }

    @Override
    public void init() {
        initAd(com.ashomok.lullabies.Settings.isAdActive);
    }

    private void requestNewInterstitial() {

        AdRequest adRequest;

        adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onSwipeOutAtStart() {
        Log.d(TAG, "onSwipeOutAtStart()");
    }


    @Override
    public void onSwipeOutAtEnd() {
        Log.d(TAG, "onSwipeOutAtEnd()");

        if (com.ashomok.lullabies.Settings.isAdActive) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
}
