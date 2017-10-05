package com.hernandez.mickael.moodtracker.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mickael Hernandez on 23/09/2017.
 */

/** Represents a mood for a specific day, that can hold a comment */
public class DayMood {

    /** The mood */
    private Mood mMood;

    /** The date */
    private Date mDate;

    /** The comment */
    private String mComment;

    DayMood(){ // Constructor by default
        mMood = Mood.NORMAL;
        mDate = Calendar.getInstance().getTime(); // Today
        mComment = "";
    }


    // Getters

    public Mood getMood() {
        return mMood;
    }

    public Date getDate() {
        return mDate;
    }

    public String getComment() {
        return mComment;
    }

    // Setters

    public void setMood(Mood mood) {
        mMood = mood;
    }

    void setDate(Date date) {
        mDate = date;
    }

    void setComment(String comment) {
        mComment = comment;
    }
}
