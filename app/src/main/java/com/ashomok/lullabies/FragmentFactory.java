package com.ashomok.lullabies;

import android.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iuliia on 31.03.2016.
 */
class FragmentFactory {

    public static Map<Integer, MusicFragmentSettings> musicFragmentSettingsList = new HashMap<Integer, MusicFragmentSettings>() {{
        put(0, new MusicFragmentSettings(R.color.tag_red, R.drawable.light_switch_off, R.raw.track1_1));
        put(1, new MusicFragmentSettings(R.color.tag_green, R.drawable.light_switch_on, R.raw.track1_2));
        put(2, new MusicFragmentSettings(R.color.tag_sky_blue, R.drawable.light_switch_off, R.raw.track1_3));
    }};

    static Fragment newInstance(int page) {

        Fragment fragment = MusicFragment.newInstance(
                musicFragmentSettingsList.get(page));
        return fragment;
    }
}
