package com.hernandez.mickael.moodtracker.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hernandez.mickael.moodtracker.model.DayMood;
import com.hernandez.mickael.moodtracker.model.Mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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

    public ArrayList<DayMood> getHistory() { // used in HistoryActivity
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

    public ArrayList<DayMood> getAllMoods() { // Used in Chart
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
        long res;
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD, mood.getMood().getId());
        values.put(KEY_COMMENT, mood.getComment());
        int index = isDaySaved(mood.getDate());
        if(index > -1) {
            res = db.update(HISTORY_TABLE_NAME, values, KEY_ID + "=" + index, null); // updates
        } else {
            values.put(KEY_DATE, mood.getDate().getTime());
            // insert values in the table
            res = db.insert(HISTORY_TABLE_NAME, null, values);
        }
        if (res == -1) {
            return false;
        } else {
            return true;
        }
    }

    private int isDaySaved(Date pDate){ // returns -1 if not saved, or the id
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        int res = -1;
        boolean found = false;
        if(c.moveToFirst()){
            do {
                Date date = new Date(c.getLong(c.getColumnIndex(KEY_DATE)));
                if(fmt.format(pDate).equals(fmt.format(date))){ // if this element's date is the same day
                    res = c.getInt(c.getColumnIndex(KEY_ID));
                    found = true;
                }
            } while(c.moveToNext() && !found);
        }
        c.close();
        return res;
    }

    private boolean isSameDay(Date pDate1, Date pDate2) {
        return DAYS.convert(pDate1.getTime() - pDate2.getTime(), MILLISECONDS) > 0;
    }
}
