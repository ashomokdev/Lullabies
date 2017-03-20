package com.ashomok.lullabies.services;

import android.content.Context;

import com.ashomok.lullabies.R;
import com.ashomok.lullabies.FragmentData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by iuliia on 2/16/17.
 */

public class PlaybackTest {

    private Context context;
    private Playback playback;

    @Mock
    FragmentData mMockTrackData;

    @Before
    public void init() {
        context = getInstrumentation().getTargetContext();
        playback = new PlaybackImpl(context);
        MockitoAnnotations.initMocks(this);
        when(mMockTrackData.getTrack()).thenReturn(R.raw.track1_1);
    }

    @Test
    public void play() throws Exception {
        playback.play(mMockTrackData);
        Thread.sleep(3000); //wait for player prepared
        Assert.assertTrue(playback.isPlaying());
    }

    @Test
    public void pause() throws Exception {

        playback.play(mMockTrackData);
        Thread.sleep(3000); //wait for player prepared
        Assert.assertTrue(playback.isPlaying());
        playback.pause();
        Assert.assertFalse(playback.isPlaying());

    }

    @Test
    public void stop() throws Exception {

        playback.play(mMockTrackData);
        Thread.sleep(3000); //wait for player prepared
        Assert.assertTrue(playback.isPlaying());
        playback.stop();
        Assert.assertFalse(playback.isPlaying());
    }

    @Test
    public void playAfterStop() throws Exception {

        playback.play(mMockTrackData);
        Thread.sleep(3000); //wait for player prepared
        Assert.assertTrue(playback.isPlaying());
        playback.stop();
        Assert.assertFalse(playback.isPlaying());
        playback.play(mMockTrackData);
        Thread.sleep(3000); //wait for player prepared
        Assert.assertTrue(playback.isPlaying());
    }

}