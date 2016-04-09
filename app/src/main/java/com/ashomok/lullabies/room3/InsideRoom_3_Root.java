package com.ashomok.lullabies.room3;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.lullabies.R;
import com.ashomok.lullabies.room1.InsideRoom_1_Fragment;

/**
 * Created by iuliia on 08.04.16.
 */
public class InsideRoom_3_Root extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.room_root_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.root_frame, new InsideRoom_3_Fragment());

        transaction.commit();

        return view;
    }
}
