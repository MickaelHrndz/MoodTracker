package com.hernandez.mickael.moodtracker.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.Toast;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.model.HistoryOpenHelper;
import com.hernandez.mickael.moodtracker.model.Mood;
import com.hernandez.mickael.moodtracker.view.VerticalViewPager;

import java.util.Calendar;
import java.util.Date;

/** Main activity */
public class MainActivity extends FragmentActivity {

    /** The number of pages (moods) to show */
    public static final int NUM_PAGES = 5;

    /** Maximum number of characters in a comment */
    public static final int MAX_CHAR_COMMENT = 256;

    /** History activity code for the intent */
    public static final int HISTORY_ACTIVITY_REQUEST_CODE = 1;

    /** Key to the shared preferences */
    public static final String PREFERENCES_KEY = "MoodTracker";

    /** SharedPreferences key for the selected mood */
    public static final String PREFERENCES_KEY_MOOD = "mood";

    /** The sounds id array for the moods */
    public int[] mSoundArray = { R.raw.sad, R.raw.disappointed, R.raw.normal, R.raw.happy, R.raw.super_happy };

    /** The media player to play sounds */
    private MediaPlayer mMediaPlayer;

    /** The SQLite database custom object to handle data saving */
    private HistoryOpenHelper mHistory = new HistoryOpenHelper(this);

    /** The object which holds preferences, in this case, the selected mood */
    private SharedPreferences mSharedPrefs;

    /** The alarm manager used to schedule an intent at a certain hour */
    private AlarmManager mAlarmMgr;

    /** The intent used by the alarm manager */
    private PendingIntent mAlarmIntent;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     *  and next wizard steps.
     */
    private VerticalViewPager mPager;

    /** The custom pager adapter */
    private ScreenSlidePagerAdapter mPagerAdapter;

    /** AlertDialog builder to let the user add a comment to its mood */
    private AlertDialog.Builder mBuilder;

    /** AlertDialog object */
    private AlertDialog mDialog;

    /** Text field to enter a comment */
    private EditText mCommentEditText;

    //public static final String PREFS_CODE = "MoodTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        // If the mood has not been updated today, set the current mood to the default one
        if(mHistory.isDaySaved(new Date()) < 0){
            mSharedPrefs.edit().putInt(PREFERENCES_KEY_MOOD, Mood.DEFAULT_MOOD).apply();
        }

        // Scheduling alarm to save mood everyday
        Calendar mResetTime = Calendar.getInstance();
        mResetTime.set(Calendar.HOUR_OF_DAY, 13);
        mResetTime.set(Calendar.MINUTE, 32);
        mAlarmMgr = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mAlarmMgr.set(AlarmManager.RTC, mResetTime.getTimeInMillis(), mAlarmIntent);

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
                if(mCommentEditText.getText().length() <= MAX_CHAR_COMMENT) {
                    mHistory.updateComment(mCommentEditText.getText().toString()); // updates today's mood comment
                } else {
                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.comment_too_long), MAX_CHAR_COMMENT), Toast.LENGTH_SHORT).show();
                }
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

    /** Saves the current mood in both SharedPreferences and SQLite database */
    private void saveMood(){
        // saves current mood in SharedPreferences
        mSharedPrefs.edit().putInt(PREFERENCES_KEY_MOOD, mPager.getCurrentItem()).apply();
        // saves current mood in database
        mHistory.updateMood(mPager.getCurrentItem());
    }

    /** Sets the current mood according to the id passed, saves it */
    public void setMood(int mood){
        mPager.setCurrentItem(mood);
        saveMood();
    }

    @Override
    public void onPause() {
        saveMood();
        super.onPause();
    }

    /** Plays the sound corresponding to the selected mood */
    private void playMoodSound(int position){
        mMediaPlayer = MediaPlayer.create(this, mSoundArray[position]);
        mMediaPlayer.start();
    }

    /** Returns comment string from today's mood */
    public String getTodayComment(){
        return mHistory.getHistory().get(0).getComment();
    }

     /*public void resetMood(){
        saveMood(Mood.DEFAULT_MOOD);
    }*/

    /** custom pageradapter class using MoodFragment */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
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
