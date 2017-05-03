package com.ashomok.lullabies.ad;

import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdView;

/**
 * Created by iuliia on 7/25/16.
 */
public interface AdContainer  {
    void initAd(ViewGroup parentLayout);

    void initBottomBanner(@Nullable AdView ad);
}
