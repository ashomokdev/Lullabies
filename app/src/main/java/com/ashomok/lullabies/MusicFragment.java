package com.ashomok.lullabies;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.Random;

/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;

    static MusicFragment newInstance(int page) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment, null);

        view.setBackgroundColor(backColor);

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        //TODO
        imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.play);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MediaPlayerService.class);
                intent.putExtra("music_res_id", R.raw.track2_1);
                getActivity().startService(intent);
            }
        });

        return view;
    }

}
