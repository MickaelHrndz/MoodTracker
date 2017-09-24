package com.hernandez.mickael.moodtracker.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.model.DayMood;
import com.hernandez.mickael.moodtracker.model.Mood;
import com.hernandez.mickael.moodtracker.view.MoodFragment;
import com.hernandez.mickael.moodtracker.view.VerticalViewPager;

import static java.lang.Long.valueOf;

public class MainActivity extends FragmentActivity {

    /** The number of pages (moods) to show **/
    private static final int NUM_PAGES = 5;

    /** History activity code for the intent **/
    public static final int HISTORY_ACTIVITY_REQUEST_CODE = 1;

    /** SharedPreferences key for the selected mood **/
    public static final String PREFERENCES_KEY_MOOD = "mood";

    /** The SQLite database custom object to handle data saving **/
    private HistoryOpenHelper mHistory;

    /** The object which holds preferences, in this case, the selected mood **/
    private SharedPreferences mSharedPrefs;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     *  and next wizard steps.
     **/
    private VerticalViewPager mPager;

    /** The pager adapter, which provides the pages to the view pager widget. **/
    private PagerAdapter mPagerAdapter;

    /** AlertDialog builder to let the user add a comment to its mood **/
    private AlertDialog.Builder mBuilder;

    /** UI Buttons from the main activity **/
    private ImageButton mCommentBtn; // Button to add a comment to the mood
    private ImageButton mHistoryBtn; // Button to open the history activity

    /** Text field to enter a comment **/
    private EditText mCommentEditText;

    public static final String PREFS_CODE = "MoodTracker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistory = new HistoryOpenHelper(this);
        mSharedPrefs = getPreferences(MODE_PRIVATE);

        // UI views links
        mCommentBtn = (ImageButton)findViewById(R.id.comment_button);
        mHistoryBtn = (ImageButton)findViewById(R.id.history_button);

        // AlertDialog builder configuration
        mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.comment);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_comment, null); // inflating custom dialog view from XML
        mBuilder.setView(dialogView);
        mCommentEditText = (EditText)dialogView.findViewById(R.id.comment_editText); // comment field

        // Add the buttons to the alert dialog
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                DayMood mood = new DayMood(getCurrentMood(), mCommentEditText.getText().toString());
                mHistory.addMood(mood); // saves mood to the database
            }
        });
        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Add a click listener to the button
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuilder.create().show(); // creates and shows the dialog
            }
        });

        mHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(historyIntent, HISTORY_ACTIVITY_REQUEST_CODE);
            }
        });
        // Instantiate a ViewPager and a PagerAdapter
        mPager = (VerticalViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mSharedPrefs.getInt(PREFERENCES_KEY_MOOD, 3));
    }

    /**
     * A pager adapter that represents [NUM_PAGES] ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MoodFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onPause() {
        mSharedPrefs.edit().putInt(PREFERENCES_KEY_MOOD, mPager.getCurrentItem()).apply();
        super.onPause();
    }
    // TODO : Everytime the user switches mood, put the mood in a general attribute and save it to sharedprefs too
    private Mood getCurrentMood(){
        return Mood.getById(valueOf(mPager.getCurrentItem()));
        /*Fragment f = getSupportFragmentManager().findFragmentById(mPager.getCurrentItem());
        return Mood.getById(valueOf(f.getArguments().getInt("mood")));*/
    }
}
