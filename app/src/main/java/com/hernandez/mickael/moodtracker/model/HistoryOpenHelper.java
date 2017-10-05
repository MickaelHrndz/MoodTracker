package com.hernandez.mickael.moodtracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hernandez.mickael.moodtracker.controller.HistoryActivity;
import com.hernandez.mickael.moodtracker.model.DayMood;
import com.hernandez.mickael.moodtracker.model.Mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mickael Hernandez on 23/09/2017.
 */

/** Custom class holding a custom SQLite table */
public class HistoryOpenHelper extends SQLiteOpenHelper {

    /** SQLite Database version */
    private static final int DATABASE_VERSION = 2;

    /** Table name */
    private static final String HISTORY_TABLE_NAME = "history";

    /** Primary key (int autoincrement) */
    private static final String KEY_ID = "_id";

    /** Mood id (int) */
    private static final String KEY_MOOD = "mood";

    /** Date.getTime() (long) */
    private static final String KEY_DATE = "date";

    /** Mood comment (String) */
    private static final String KEY_COMMENT = "comment";

    /** SQL query for table creation */
    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_MOOD + " INTEGER, " +
                    KEY_DATE + " INTEGER, " +
                    KEY_COMMENT + " TEXT );";

    public HistoryOpenHelper(Context context) { // Constructor
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

    /** Used in HistoryActivity, returns the last [HISTORY_MAX_ROWS] moods */
    public ArrayList<DayMood> getHistory() {
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


    /** Updates today's mood */
    public boolean updateMood(int idMood){
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

    /** Updates today's comment */
    public boolean updateComment(String comment){
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

    /** Returns -1 if not found, or the id */
    private int isDaySaved(Date pDate){
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
}
