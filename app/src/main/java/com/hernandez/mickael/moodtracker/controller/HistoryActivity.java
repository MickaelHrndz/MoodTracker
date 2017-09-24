package com.hernandez.mickael.moodtracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.model.DayMood;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    public static final int HISTORY_MAX_ROWS = 7;

    /** The SQLite database custom object to handle data saving **/
    private HistoryOpenHelper mHistory;

    /** The mood array to hold the history **/
    private ArrayList<DayMood> mDayMoods;

    /** The history list component **/
    private ListView mListView;

    /** The adapter to populate the listview **/
    private HistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mListView = (ListView)findViewById(R.id.history_listview);

        // History data
        mHistory = new HistoryOpenHelper(this);
        mDayMoods = mHistory.getHistory();
        /*while(mDayMoods.size() > HISTORY_MAX_ROWS){ // deleting
            mDayMoods.remove(0);
        }
        Collections.reverse(mDayMoods); // newest elements first*/

        // Using custom adapter
        mHistoryAdapter = new HistoryAdapter(this, mDayMoods);
        mListView.setAdapter(mHistoryAdapter);
    }
}
