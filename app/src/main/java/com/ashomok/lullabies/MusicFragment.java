package com.ashomok.lullabies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.lullabies.rate_app.RateAppAsker;
import com.ashomok.lullabies.tools.LogHelper;


/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(MusicFragment.class);

    static MusicFragment newInstance(int position) {
        return new MusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RateAppAsker.init(getActivity());
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.music_fragment, null);
    }

}
