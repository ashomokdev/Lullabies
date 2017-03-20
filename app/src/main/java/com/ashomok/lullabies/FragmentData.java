package com.ashomok.lullabies;

/**
 * Created by Iuliia on 03.05.2016.
 */
public class FragmentData {

    private int mainColor;
    private int track;
    private int image;
    private String title;

    public int getID() {
        return ID;
    }

    private int ID;

    public FragmentData(int mainColor, int image, int track) {
        this.mainColor = mainColor;
        this.image = image;
        this.track = track;
    }

    public int getTrack() {
        return track;
    }

    public int getMainColor() {
        return mainColor;
    }

    public int getImage() {
        return image;
    }
}
