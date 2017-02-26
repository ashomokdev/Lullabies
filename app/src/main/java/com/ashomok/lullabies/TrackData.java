package com.ashomok.lullabies;

/**
 * Created by Iuliia on 03.05.2016.
 */
public class TrackData {
    private int backgroundPattern;
    private int track;
    private int image;
    private String title;

    public int getID() {
        return ID;
    }

    private int ID;

    public TrackData(int backgroundPattern, int image, int track) {
        this.backgroundPattern = backgroundPattern;
        this.image = image;
        this.track = track;
    }

    public int getTrack() {
        return track;
    }

    public int getBackgroundPattern() {
        return backgroundPattern;
    }

    public int getImage() {
        return image;
    }
}
