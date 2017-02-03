package com.ashomok.lullabies;

import android.content.Context;
import android.media.AudioManager;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FragmentPagerSupportActivityTest {

    @Rule
    public ActivityTestRule<FragmentPagerSupportActivity> mActivityRule = new ActivityTestRule<>(
            FragmentPagerSupportActivity.class);

    @Test
    public void testFabBtn() {
        //press the button.
        onView(withId(R.id.fab)).perform(click()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {
                AudioManager manager = (AudioManager) mActivityRule.getActivity().getSystemService(Context.AUDIO_SERVICE);
                if (!manager.isMusicActive()) {
                    fail("Music is not playing!");
                }
            }
        });
    }

    @Test
    public void testSwitchMusic() {

        final ViewPager viewPager = (ViewPager) mActivityRule.getActivity().findViewById(R.id.pager);
        final int startItem = viewPager.getCurrentItem();

        onView(withId(R.id.pager)).perform(swipeLeft()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {

                int currentItem = viewPager.getCurrentItem();
                assertTrue(currentItem == startItem + 1);

            }
        });
    }
}