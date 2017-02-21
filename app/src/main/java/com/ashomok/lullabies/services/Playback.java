package com.ashomok.lullabies.services;

/**
 * Created by iuliia on 2/14/17.
 */

import com.ashomok.lullabies.TrackData;

/**
 * Interface representing Playback. The {@link MediaPlayerService} works
 * directly with an instance of the Playback object to make the various calls such as
 * play, pause etc.
 */
public interface Playback {
    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     */
    void stop();


    /**
     * @return boolean that indicates that this is ready to be used.
     */
    boolean isConnected();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();


    /**
     *
     * @param track to play
     */
    void play(TrackData track);

    /**
     * Pause the current playing item
     */
    void pause();


    /**
     * @param callback to be called
     */
    void setCallback(MediaPlayerServiceTools.MediaPlayerCallback callback);
}
