package com.hernandez.mickael.moodtracker.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hernandez.mickael.moodtracker.model.DayMood;
import com.hernandez.mickael.moodtracker.model.Mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mickael Hernandez on 23/09/2017.
 */

class HistoryOpenHelper extends SQLiteOpenHelper { // Custom class holding a custom SQLite table

    private static final int DATABASE_VERSION = 2; // SQLite Database version
    private static final String HISTORY_TABLE_NAME = "history"; // Table name
    private static final String KEY_ID = "_id"; // Primary key (int autoincrement)
    private static final String KEY_MOOD = "mood"; // Mood id (int)
    private static final String KEY_DATE = "date";  // Date.getTime() (long)
    private static final String KEY_COMMENT = "comment"; // Mood comment (String)

    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_MOOD + " INTEGER, " +
                    KEY_DATE + " INTEGER, " +
                    KEY_COMMENT + " TEXT );"; // SQL query for table creation

    HistoryOpenHelper(Context context) { // Constructor
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

    ArrayList<DayMood> getHistory() { // used in HistoryActivity, retrieves the last [HISTORY_MAX_ROWS] moods
        ArrayList<DayMood> moodsArrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        int i = 0;
        if (c.moveToLast()) {
            do {
                DayMood mood = new DayMood();
                mood.setMood(Mood.getById(c.getLong(c.getColumnIndex(KEY_MOOD)))); // Mood
                mood.setDate(new Date(c.getLong(c.getColumnIndex(KEY_DATE)))); // Date
                mood.setComment(c.getString(c.getColumnIndex(KEY_COMMENT))); // Comment
                // adding to moods list
                moodsArrayList.add(mood);
                i++;
            } while (c.moveToPrevious() && i < HistoryActivity.HISTORY_MAX_ROWS); // getting the first [HISTORY_MAX_ROWS] moods
        }
        c.close();
        return moodsArrayList;
    }

    /*public ArrayList<DayMood> getAllMoods() { // Used in Chart
        ArrayList<DayMood> moodsArrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToLast()) {
            do {
                DayMood mood = new DayMood();
                mood.setMood(Mood.getById(c.getLong(c.getColumnIndex(KEY_MOOD)))); // Mood
                mood.setDate(new Date(c.getLong(c.getColumnIndex(KEY_DATE)))); // Date
                //Log.d("mood date", mood.getDate().toString());
                mood.setComment(c.getString(c.getColumnIndex(KEY_COMMENT))); // Comment
                // adding to moods list
                moodsArrayList.add(mood);
            } while (c.moveToPrevious());
        }
        c.close();
        return moodsArrayList;
    }*/

    private int isDaySaved(Date pDate){ // returns -1 if not found, or the id
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

    /*public boolean addDayMood(DayMood mood) { // Used before, dropped for updateMood and updateComment
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD, mood.getMood().getId());
        values.put(KEY_COMMENT, mood.getComment());
        int index = isDaySaved(mood.getDate());
        if(index > -1) {
            return db.update(HISTORY_TABLE_NAME, values, KEY_ID + "=" + index, null) > 0; // updates
        } else {
            values.put(KEY_DATE, mood.getDate().getTime());
            // insert values in the table
            return db.insert(HISTORY_TABLE_NAME, null, values) > 0;
        }
    }*/

    boolean updateMood(int idMood){ // updates today's mood
        Date now = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD, idMood);
        int index = isDaySaved(now);
        if(index > -1) {
            return db.update(HISTORY_TABLE_NAME, values, KEY_ID + "=" + index, null) > 0; // updates
        } else {
            values.put(KEY_DATE, now.getTime());
            return db.insert(HISTORY_TABLE_NAME, null, values) > 0; // insert new row
        }
    }


    boolean updateComment(String comment){ // updates today's comment
        Date now = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COMMENT, comment); // adding comment
        int index = isDaySaved(now);
        if(index > -1) {
            return db.update(HISTORY_TABLE_NAME, values, KEY_ID + "=" + index, null) > 0; // updates
        } else {
            values.put(KEY_DATE, now.getTime());
            return db.insert(HISTORY_TABLE_NAME, null, values) > 0; // insert new row
        }
    }
}
