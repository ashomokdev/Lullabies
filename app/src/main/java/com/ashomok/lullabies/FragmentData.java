package com.ashomok.lullabies;

/**
 * Created by Iuliia on 03.05.2016.
 */
public class FragmentData {

    private int track;
    private int image;

    public FragmentData(int image, int track) {
        this.image = image;
        this.track = track;
    }

    public int getTrack() {
        return track;
    }
    public int getImage() {
        return image;
    }
}
