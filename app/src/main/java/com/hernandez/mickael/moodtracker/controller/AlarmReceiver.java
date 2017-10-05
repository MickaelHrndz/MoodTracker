package com.hernandez.mickael.moodtracker.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hernandez.mickael.moodtracker.model.HistoryOpenHelper;
import com.hernandez.mickael.moodtracker.model.Mood;

/**
 * Created by Mickaël Hernandez on 29/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { // when the alarm is fired
        /*MainActivity activity = new MainActivity();
        activity.resetMood();*/

        // Saving in SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        prefs.edit().putInt(MainActivity.PREFERENCES_KEY_MOOD, Mood.DEFAULT_MOOD).apply();

        // Saving in database
        HistoryOpenHelper mHistory = new HistoryOpenHelper(context);
        mHistory.updateMood(Mood.DEFAULT_MOOD);
    }
}
