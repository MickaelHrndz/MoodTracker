package com.hernandez.mickael.moodtracker.controller;

import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;

import com.hernandez.mickael.moodtracker.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by Mickaël Hernandez on 03/10/2017.
 */

/** This test must be done after the MainActivityTest */
public class HistoryActivityTest {

    @Rule
    public ActivityTestRule<HistoryActivity> mActivityTestRule = new ActivityTestRule<>(HistoryActivity.class);

    private HistoryActivity mActivity;

    /** Prepares the activity */
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    /** Clicks on first row's imageview displays a toast with "test" text */
    @Test
    public void test_toast_comment() throws Exception {
        onData(allOf()).atPosition(0).
                onChildView(withId(R.id.row_imageview)).
                perform(click());
        onView(withText("test")).inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /** closes the activity */
    @After
    public void tearDown() throws Exception {
        mActivity.finish();
    }
}