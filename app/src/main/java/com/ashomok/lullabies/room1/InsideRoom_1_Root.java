package com.ashomok.lullabies.room1;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.lullabies.R;

/**
 * Created by Iuliia on 06.04.2016.
 */
public class InsideRoom_1_Root extends Fragment{
    private static final String TAG = InsideRoom_1_Root.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.room_root_1_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.root_frame, new InsideRoom_1_Fragment());

        transaction.addToBackStack(null);

        transaction.commit();

        return view;
    }

}
