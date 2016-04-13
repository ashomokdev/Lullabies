package com.ashomok.lullabies;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ImageView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FragmentPagerSupportActivityTest {

    @Rule
    public ActivityTestRule<FragmentPagerSupportActivity> mActivityRule = new ActivityTestRule<>(
            FragmentPagerSupportActivity.class);

//    @Before
//    public void initValidString() {
//        // Specify a valid string.
//        mStringToBetyped = "Espresso";
//    }

//    @Test
//    public void changeText_sameActivity() {
//        //press the button.
//        onView(withId(R.id.image_background_dark)).perform(click()).check(new ViewAssertion() {
//            @Override
//            public void check(View view, NoMatchingViewException e) {
//                AudioManager manager = (AudioManager) mActivityRule.getActivity().getSystemService(Context.AUDIO_SERVICE);
//                if (! manager.isMusicActive()) {
//                    fail("Music is not playing!");
//                }
//            }
//        });
//    }

    @Test
    public void checkLight_switch() {
        //press the button.
        onView(withId(R.id.light_switch_btn)).perform(click()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {

                ImageView background = (ImageView) mActivityRule.getActivity().findViewById(R.id.image_background);

                if (!background.getDrawable().getConstantState().equals(mActivityRule.getActivity().getResources().getDrawable(R.drawable.background1_dark).getConstantState())) {
                    fail("room1 was not switch to dark mode when expected.");
                }
            }
        });

        onView(withId(R.id.light_switch_btn)).perform(click()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {
                ImageView background = (ImageView) mActivityRule.getActivity().findViewById(R.id.image_background);

                if (!background.getDrawable().getConstantState().equals(mActivityRule.getActivity().getResources().getDrawable(R.drawable.background1).getConstantState())) {
                    fail("room1 was not switch to default mode when expected.");
                }
            }
        });
    }

    @Test
    public void checkNextBackBtns() {
        int pagesCount = FragmentPagerSupportActivity.NUM_ITEMS;
        if (pagesCount < 2) {

            onView(withId(R.id.btn_back)).check(
                    matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.btn_next)).check(
                    matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        } else {

            onView(withId(R.id.btn_back)).check(
                    matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            final ImageView backgroundOld = (ImageView) mActivityRule.getActivity().findViewById(R.id.image_background);

            onView(withId(R.id.btn_next)).perform(click()).check(new ViewAssertion() {

                @Override
                public void check(View view, NoMatchingViewException e) {
                    ImageView background = (ImageView) mActivityRule.getActivity().findViewById(R.id.image_background);

                    if (background.getDrawable().getConstantState().equals(backgroundOld.getDrawable().getConstantState())) {
                        fail("switching to next room not switch background");
                    }
                }

            });

        }
    }
}