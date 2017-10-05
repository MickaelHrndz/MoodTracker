package com.hernandez.mickael.moodtracker.controller;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;


import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.view.VerticalViewPager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by Mickaël Hernandez on 02/10/2017.
 */

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity;

    private SharedPreferences mSharedPreferences;

    private VerticalViewPager mPager;

    /** Prepares the activity */
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        mSharedPreferences = mActivity.getApplicationContext().getSharedPreferences(MainActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        mPager = (VerticalViewPager)mActivity.findViewById(R.id.pager);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.setMood(0);
            }
        });
    }

    /** There is a correct number of pages in the pager's adapter */
    @Test
    public void test_mood_views() throws Exception {
        assertTrue(mPager.getAdapter().getCount() == MainActivity.NUM_PAGES);
    }

    /** The swipe changes the mood */
    @Test
    public void test_swipe() throws Exception {
        //int beforeMood = mSharedPreferences.getInt(MainActivity.PREFERENCES_KEY_MOOD, Mood.DEFAULT_MOOD);
        int beforeMood = mPager.getCurrentItem();
        onView(withId(R.id.pager)).perform(swipeUp());
        Thread.sleep(500); // animation delay
        int afterMood = mPager.getCurrentItem();
        //int afterMood = mSharedPreferences.getInt(MainActivity.PREFERENCES_KEY_MOOD, Mood.DEFAULT_MOOD);
        assertNotEquals(beforeMood, afterMood);
    }

    /** Opens the comment dialog, adds a comment and checks if it has been updated */
    @Test
    public void test_comment() throws Exception {
        String comment = "test";
        // click on the comment button
        onView(withId(R.id.comment_button)).perform(click());

        // writes a comment
        onView(withId(R.id.comment_editText)).perform(typeText(comment));

        onView(withText(R.string.ok))
        .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        assertEquals(comment, mActivity.getTodayComment());
    }

    /** The history button starts HistoryActivity */
    @Test
    public void test_launch_history() throws Exception {

        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(HistoryActivity.class.getName(), null, false);

        // click on the history button
        onView(withId(R.id.history_button)).perform(click());

        //Watch for the timeout
        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

        // if nextActivity isn't null, a HistoryActivity has opened
        assertNotNull(nextActivity);
        nextActivity.finish();
    }

    /** Finishes the activity */
    @After
    public void tearDown() throws Exception {
        mActivity.finish();
    }
}