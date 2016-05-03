package com.ashomok.lullabies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ashomok.lullabies.tools.BitmapWorkerTask;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by iuliia on 03.05.16.
 */
public class MusicFragment extends Fragment {

    public static Map<Integer, MusicFragmentSettings> musicFragmentSettingsList = new HashMap<Integer, MusicFragmentSettings>() {{
        put(0, new MusicFragmentSettings(R.color.white, R.drawable.light_switch_off, R.raw.track1_1));
        put(1, new MusicFragmentSettings(R.color.tag_green, R.drawable.light_switch_on, R.raw.track1_2));
        put(2, new MusicFragmentSettings(R.color.white, R.drawable.light_switch_off, R.raw.track1_3));
    }};

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    private static final String ARGUMENT_BACKGROUND_COLOR = "arg_background_color";
    private static final String ARGUMENT_IMAGE = "arg_image";

    private int pageNumber;
    private int backColor;
    private int image;


    static MusicFragment newInstance(int page) {
        MusicFragment pageFragment = new MusicFragment();
        Bundle arguments = new Bundle();
        MusicFragmentSettings settings = musicFragmentSettingsList.get(page);

        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putInt(ARGUMENT_BACKGROUND_COLOR, settings.getBackgroundColor());
        arguments.putInt(ARGUMENT_IMAGE, settings.getImage());

        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        backColor = getArguments().getInt(ARGUMENT_BACKGROUND_COLOR);
        image = getArguments().getInt(ARGUMENT_IMAGE);
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

        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);

    }
}
