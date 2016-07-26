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
public class AdContainerImpl implements AdContainer, CustomViewPager.OnSwipeOutListener {

    private static final String TAG = AdContainerImpl.class.getSimpleName();
    private final Activity context;
    private InterstitialAd mInterstitialAd;

    public AdContainerImpl(Activity context)
    {
        this.context = context;

        initAd(com.ashomok.lullabies.Settings.isAdActive);

    }

    @Override
    public void initAd(boolean isAdActive) {
        if (isAdActive) {
            if (context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                //init banner

                MobileAds.initialize(context.getApplicationContext(), com.ashomok.lullabies.Settings.appID);
                AdView mAdView = (AdView) context.findViewById(R.id.adBannerView);

                AdRequest adRequest;
                if (com.ashomok.lullabies.Settings.isAdInTestMode) {
                    adRequest = new AdRequest.Builder().addTestDevice("66CCD973A08D78774D4713B9443E93F4").build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }

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

    private void requestNewInterstitial() {

        AdRequest adRequest;

        if (com.ashomok.lullabies.Settings.isAdInTestMode) {
            adRequest = new AdRequest.Builder().addTestDevice("66CCD973A08D78774D4713B9443E93F4").build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

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
