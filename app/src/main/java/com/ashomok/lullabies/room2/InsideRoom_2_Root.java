package com.ashomok.lullabies.room2;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.lullabies.R;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_2_Root extends Fragment {

    private static final String TAG = InsideRoom_2_Root.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.room_root_2_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.root_frame_2, new InsideRoom_2_Fragment());

        transaction.addToBackStack(null);

        transaction.commit();

        Log.d(TAG, "View created");
        return view;
    }
}
