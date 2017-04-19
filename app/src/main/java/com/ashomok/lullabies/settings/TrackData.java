package com.ashomok.lullabies.settings;

/**
 * Created by iuliia on 4/19/17.
 */

public class TrackData {
    private String title;
    private String album;
    private String artist;
    private String genre;
    private long trackNumber;
    private int imageDrawableId;
    private int source;
    private long totalTrackCount;
    private long durationMs;

    public TrackData(String title, String album, String artist, String genre, int source,
                     int imageDrawableId, int trackNumber, int totalTrackCount, int durationMs) {
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.genre = genre;
        this.trackNumber = trackNumber;
        this.imageDrawableId = imageDrawableId;
        this.source = source;
        this.totalTrackCount = totalTrackCount;
        this.durationMs = durationMs;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public long getTrackNumber() {
        return trackNumber;
    }

    public int getImageDrawableId() {
        return imageDrawableId;
    }

    public int getSource() {
        return source;
    }

    public long getTotalTrackCount() {
        return totalTrackCount;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
