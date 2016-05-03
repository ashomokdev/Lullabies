package com.ashomok.lullabies;

/**
 * Created by Iuliia on 03.05.2016.
 */
public class MusicFragmentSettings {
    private int backgroundColor;
    private int track;
    private int image;

    public MusicFragmentSettings(int backgroundColor, int image, int track) {
        this.backgroundColor = backgroundColor;
        this.image = image;
        this.track = track;
    }

    public int getTrack() {
        return track;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getImage() {
        return image;
    }
}
