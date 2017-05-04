package com.ashomok.lullabies.ad;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ashomok.lullabies.R;
import com.ashomok.lullabies.settings.Settings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by iuliia on 7/26/16.
 */
public class AdMobContainerImpl implements AdContainer {


    public final String appID;

    private static final String TAG = AdMobContainerImpl.class.getSimpleName();
    private final Activity context;

    public AdMobContainerImpl(Activity context) {
        this.context = context;
        appID = context.getResources().getString(R.string.appID);

        MobileAds.initialize(context, appID);
    }

    /**
     * add bottom banner on the parent view. Note: It may overlay some views.
     *
     * @param parent
     */
    private void addBottomBanner(ViewGroup parent) {
        if (parent instanceof RelativeLayout || parent instanceof LinearLayout) {
            AdView adView = new AdView(context);
            if (parent instanceof RelativeLayout) {

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                adView.setLayoutParams(lp);
            } else if (parent instanceof LinearLayout) {
                adView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f));

            }

            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id));
            adView.setId(R.id.ad_banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            parent.addView(adView);
        } else {
            Log.e(TAG, "Ads can not been loaded programmaticaly. RelativeLayout and LinearLayout are supported as parent.");
        }

    }

    /**
     * init ad with bottom banner. Note: It may overlay some view.
     *
     * @param parentLayout
     */
    @Override
    public void initAd(ViewGroup parentLayout) {
        initAd(Settings.isAdActive, parentLayout);

    }

    /**
     * @param mAdView - banner
     */
    @Override
    public void initBottomBanner(@Nullable final AdView mAdView) {
        if (Settings.isAdActive) {
            if (mAdView != null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    private void initAd(boolean isAdActive, ViewGroup parentLayout) {
        if (isAdActive) {

            if (context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                //init banner
                addBottomBanner(parentLayout);
            }
        }
    }
}
