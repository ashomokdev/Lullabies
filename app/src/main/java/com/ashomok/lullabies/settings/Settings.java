package com.ashomok.lullabies.settings;

import android.content.Context;

import com.ashomok.lullabies.MyApplication;
import com.ashomok.lullabies.R;

import java.util.ArrayList;

/**
 * Created by iuliia on 7/25/16.
 */
public class Settings {

    public static final boolean isAdActive = true;
    public static final boolean isAdinTestMode = true;

    private ArrayList<TrackData> musicSource = new ArrayList<>();

    public ArrayList<TrackData> getMusicSource(Context context) {
        if (musicSource.size() > 0) {
            return musicSource;
        } else {
            musicSource.add(new TrackData(context.getString(R.string.lion), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track1, R.drawable.lion2, 1, 9, 190459));
            musicSource.add(new TrackData(context.getString(R.string.koala), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track2, R.drawable.koala2, 2, 9, 204932));
            musicSource.add(new TrackData(context.getString(R.string.giraffe), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track3, R.drawable.giraffe2, 3, 9, 215355));
            musicSource.add(new TrackData(context.getString(R.string.crocodile), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track4, R.drawable.crocodile2, 4, 9, 286929));
            musicSource.add(new TrackData(context.getString(R.string.elephant), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track5, R.drawable.elephant2, 5, 9, 208405));
            musicSource.add(new TrackData(context.getString(R.string.whale), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track6, R.drawable.whale2, 6, 9, 166034));
            musicSource.add(new TrackData(context.getString(R.string.chameleon), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track7, R.drawable.chameleon2, 7, 9, 266173));
            musicSource.add(new TrackData(context.getString(R.string.turtle), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track8, R.drawable.turtle2, 8, 9, 241560));
            musicSource.add(new TrackData(context.getString(R.string.monkey), "Lullabies",
                    context.getString(R.string.lullaby_songs), "Lullabies",
                    R.raw.track9, R.drawable.monkey2, 9, 9, 224000));
            return musicSource;
        }
    }

    public ArrayList<TrackData> getMusicSource() {
        Context context = MyApplication.getAppContext();
        return getMusicSource(context);
    }
}
