package com.hernandez.mickael.moodtracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.model.DayMood;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity { // Activity that displays a history of the last days moods

    /** Max number of rows there can be in the history **/
    public static final int HISTORY_MAX_ROWS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // On activity creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        /* The history list component */
        ListView listView = (ListView) findViewById(R.id.history_listview);

        // History data

        /* The SQLite database custom object to handle data saving */
        HistoryOpenHelper history = new HistoryOpenHelper(this);

        /* The mood array to hold the history */
        ArrayList<DayMood> dayMoods = history.getHistory();

        /* The custom adapter to populate the ListView */
        HistoryAdapter historyAdapter = new HistoryAdapter(this, dayMoods);
        listView.setAdapter(historyAdapter);
    }
}
