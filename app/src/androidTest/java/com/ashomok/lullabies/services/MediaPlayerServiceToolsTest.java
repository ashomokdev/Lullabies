package com.ashomok.lullabies.services;

import android.content.Context;
import android.media.AudioManager;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.ashomok.lullabies.MainActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by iuliia on 2/13/17.
 */
public class MediaPlayerServiceToolsTest {
    private static final String TAG = MediaPlayerServiceToolsTest.class.getSimpleName();
    private MediaPlayerServiceTools mediaPlayerServiceTools;
    private AudioManager manager;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Before
    public void init() {
        Context context = mActivityRule.getActivity();
        MediaPlayerServiceTools.MediaPlayerCallback callback = new MediaPlayerServiceTools.MediaPlayerCallback() {
            @Override
            public void onCompletion() {
                Log.i(TAG, "track finished.");
            }
        };
        mediaPlayerServiceTools = new MediaPlayerServiceTools(context);
        mediaPlayerServiceTools.setCallback(callback);
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Test
    public void testPlay() throws Exception{
        mediaPlayerServiceTools.handlePlayRequest(0);
        Thread.sleep(200);
        if (!manager.isMusicActive()) {
            Assert.fail("music not playing");
        }
    }

    @Test
    public void testPause() throws Exception {
        mediaPlayerServiceTools.handlePlayRequest(0);
        Thread.sleep(200);
        mediaPlayerServiceTools.handlePauseRequest();
        Thread.sleep(200);

        if (manager.isMusicActive()) {
            Assert.fail("music not paused");
        }
    }

    @Test
    public void testStop() throws Exception{
        mediaPlayerServiceTools.handlePlayRequest(0);
        Thread.sleep(200);
        mediaPlayerServiceTools.handleStopRequest();
        Thread.sleep(200);

        if (manager.isMusicActive()) {
            Assert.fail("music not stoped");
        }
    }

}