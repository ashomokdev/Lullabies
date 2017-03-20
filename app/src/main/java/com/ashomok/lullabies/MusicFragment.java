package com.ashomok.lullabies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ashomok.lullabies.rate_app.RateAppAsker;


/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static final String ARGUMENT_BACKGROUND_PATTERN = "arg_background_color";
    private static final String ARGUMENT_IMAGE = "arg_image";
    private static final String ARGUMENT_TRACK = "arg_track";


    private int backgroundPattern;
    private int image;
    private int track;


    public int getBackgroundPattern() {
        return backgroundPattern;
    }

    public int getImage() {
        return image;
    }

    public int getTrack() {
        return track;
    }

    static MusicFragment newInstance(FragmentData settings) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();

        arguments.putInt(ARGUMENT_BACKGROUND_PATTERN, settings.getMainColor());
        arguments.putInt(ARGUMENT_IMAGE, settings.getImage());
        arguments.putInt(ARGUMENT_TRACK, settings.getTrack());

        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundPattern = getArguments().getInt(ARGUMENT_BACKGROUND_PATTERN);
        image = getArguments().getInt(ARGUMENT_IMAGE);
        track = getArguments().getInt(ARGUMENT_TRACK);

        RateAppAsker.init(getActivity());
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set background
        View view = inflater.inflate(R.layout.music_fragment, null);
        view.findViewById(R.id.music_fragment).setBackgroundColor(getResources().getColor(backgroundPattern));

        //set image
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageDrawable(getResources().getDrawable(image));

        return view;
    }

}
