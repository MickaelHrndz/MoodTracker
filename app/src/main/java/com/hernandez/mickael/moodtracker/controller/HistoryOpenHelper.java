package com.hernandez.mickael.moodtracker.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hernandez.mickael.moodtracker.model.DayMood;
import com.hernandez.mickael.moodtracker.model.Mood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Mickael Hernandez on 23/09/2017.
 */

public class HistoryOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String HISTORY_TABLE_NAME = "history";
    public static final String KEY_ID = "_id";
    public static final String KEY_MOOD = "mood";
    public static final String KEY_DATE = "date";
    public static final String KEY_COMMENT = "comment";

    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_MOOD + " INTEGER, " +
                    KEY_DATE + " INTEGER, " +
                    KEY_COMMENT + " TEXT );";

    HistoryOpenHelper(Context context) {
        super(context, HISTORY_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + HISTORY_TABLE_NAME + "'");
        onCreate(db);
    }

    public ArrayList<DayMood> getHistory() {
        ArrayList<DayMood> moodsArrayList = new ArrayList<DayMood>();
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        int i = 0;
        if (c.moveToLast()) {
            do {
                DayMood mood = new DayMood();
                mood.setMood(Mood.getById(c.getLong(c.getColumnIndex(KEY_MOOD)))); // Mood
                Log.d("mood", mood.getMood().toString());
                mood.setDate(new Date(c.getLong(c.getColumnIndex(KEY_DATE)))); // Date
                //Log.d("mood date", mood.getDate().toString());
                mood.setComment(c.getString(c.getColumnIndex(KEY_COMMENT))); // Comment
                // adding to moods list
                moodsArrayList.add(mood);
                i++;
            } while (c.moveToPrevious() && i < HistoryActivity.HISTORY_MAX_ROWS); // getting the first [HISTORY_MAX_ROWS] moods
        }
        c.close();
        Log.d("array", moodsArrayList.toString());
        return moodsArrayList;
    }

    public ArrayList<DayMood> getAllMoods() {
        ArrayList<DayMood> moodsArrayList = new ArrayList<DayMood>();
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToLast()) {
            do {
                DayMood mood = new DayMood();
                mood.setMood(Mood.getById(c.getLong(c.getColumnIndex(KEY_MOOD)))); // Mood
                Log.d("mood", mood.getMood().toString());
                mood.setDate(new Date(c.getLong(c.getColumnIndex(KEY_DATE)))); // Date
                //Log.d("mood date", mood.getDate().toString());
                mood.setComment(c.getString(c.getColumnIndex(KEY_COMMENT))); // Comment
                // adding to moods list
                moodsArrayList.add(mood);
            } while (c.moveToPrevious());
        }
        c.close();
        Log.d("array", moodsArrayList.toString());
        return moodsArrayList;
    }

    public boolean addMood(DayMood mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD, mood.getMood().getId());
        values.put(KEY_COMMENT, mood.getComment());
        values.put(KEY_DATE, mood.getDate().getTime());
        // insert values in the table
        long insert = db.insert(HISTORY_TABLE_NAME, null, values);
        if(insert == -1){
            return false;
        } else {
            return true;
        }
    }
}
