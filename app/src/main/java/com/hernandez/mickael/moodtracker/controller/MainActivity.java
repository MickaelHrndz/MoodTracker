package com.hernandez.mickael.moodtracker.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.view.VerticalViewPager;

public class MainActivity extends FragmentActivity { // Main activity

    /** The number of pages (moods) to show **/
    private static final int NUM_PAGES = 5;

    /** History activity code for the intent **/
    public static final int HISTORY_ACTIVITY_REQUEST_CODE = 1;

    /** SharedPreferences key for the selected mood **/
    public static final String PREFERENCES_KEY_MOOD = "mood";

    /** The sounds id array for the moods **/
    public int[] mSoundArray = { R.raw.sad, R.raw.disappointed, R.raw.normal, R.raw.happy, R.raw.super_happy };

    /** The media player to play sounds **/
    private MediaPlayer mMediaPlayer;

    /** The SQLite database custom object to handle data saving **/
    private HistoryOpenHelper mHistory = new HistoryOpenHelper(this);

    /** The object which holds preferences, in this case, the selected mood **/
    private SharedPreferences mSharedPrefs;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     *  and next wizard steps.
     **/
    private VerticalViewPager mPager;

    /** The custom pager adapter **/
    private ScreenSlidePagerAdapter mPagerAdapter;

    /** AlertDialog builder to let the user add a comment to its mood **/
    private AlertDialog.Builder mBuilder;

    /** AlertDialog object **/
    private AlertDialog mDialog;

    /** Text field to enter a comment **/
    private EditText mCommentEditText;

    //public static final String PREFS_CODE = "MoodTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) { // On activity creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getPreferences(MODE_PRIVATE);

        /* UI Buttons from the main activity */
        ImageButton commentBtn = (ImageButton) findViewById(R.id.comment_button);
        ImageButton historyBtn = (ImageButton) findViewById(R.id.history_button);

        // AlertDialog builder configuration
        mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.comment);

        // Adds the buttons to the alert dialog
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mHistory.updateComment(mCommentEditText.getText().toString()); // updates today's mood comment
            }
        });
        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Click listener for the comment button
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = mBuilder.create(); // creates AlertDialog instance from the builder
                View cView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_comment, null); // Inflates custom dialog view from XML
                mDialog.setView(cView);
                mCommentEditText = (EditText)cView.findViewById(R.id.comment_editText); // comment field input
                mDialog.show();
            }
        });

        // Click listener for the history button
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(historyIntent, HISTORY_ACTIVITY_REQUEST_CODE);
            }
        });

        // ViewPager and PagerAdapter
        mPager = (VerticalViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Sets the displayed mood to the one saved in SharedPrefs
        mPager.setCurrentItem(mSharedPrefs.getInt(PREFERENCES_KEY_MOOD, 3));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // Listener to play a sound every time a mood is selected
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                playMoodSound(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPause() { // On activity pause
        // saves current mood in SharedPreferences
        mSharedPrefs.edit().putInt(PREFERENCES_KEY_MOOD, mPager.getCurrentItem()).apply();
        // saves current mood in database
        mHistory.updateMood(mPager.getCurrentItem());
        super.onPause();
    }

    private void playMoodSound(int position){ // Plays the sound corresponding to the selected mood
        mMediaPlayer = MediaPlayer.create(this, mSoundArray[position]);
        mMediaPlayer.start();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter { // custom pageradapter class using MoodFragment
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
}
