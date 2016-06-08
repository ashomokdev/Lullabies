package com.ashomok.lullabies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ashomok.lullabies.tools.ImageLoaderAsyncTask;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static final String ARGUMENT_BACKGROUND_COLOR = "arg_background_color";
    private static final String ARGUMENT_IMAGE = "arg_image";
    private static final String ARGUMENT_TRACK = "arg_track";


    private int backColor;
    private int image;
    private int track;


    public int getBackColor() {
        return backColor;
    }

    public int getImage() {
        return image;
    }

    public int getTrack() {
        return track;
    }

    static MusicFragment newInstance(MusicFragmentSettings settings) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();

        arguments.putInt(ARGUMENT_BACKGROUND_COLOR, settings.getBackgroundColor());
        arguments.putInt(ARGUMENT_IMAGE, settings.getImage());
        arguments.putInt(ARGUMENT_TRACK, settings.getTrack());

        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backColor = getArguments().getInt(ARGUMENT_BACKGROUND_COLOR);
        image = getArguments().getInt(ARGUMENT_IMAGE);
        track = getArguments().getInt(ARGUMENT_TRACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_fragment, null);
        view.findViewById(R.id.music_fragment).setBackgroundColor(getResources().getColor(backColor));

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        loadBitmapAsync(image, imageView);

        return view;
    }

    protected void loadBitmapAsync(int resId, ImageView imageView) {

        ImageLoaderAsyncTask task = new ImageLoaderAsyncTask(imageView);
        task.execute(resId);

    }
}
