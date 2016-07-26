package com.ashomok.lullabies;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by iuliia on 7/25/16.
 */
public class InterstitialAdActivity extends Activity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

                //todo back to last viewpage
                //  beginPlayingGame();
            }
        });

        requestNewInterstitial();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            //
        }
    }

    private void requestNewInterstitial() {

        AdRequest adRequest;

        if (Settings.isAdInTestMode) {
            adRequest = new AdRequest.Builder().addTestDevice("66CCD973A08D78774D4713B9443E93F4").build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        mInterstitialAd.loadAd(adRequest);
    }

}
