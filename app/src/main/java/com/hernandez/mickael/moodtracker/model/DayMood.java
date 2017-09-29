package com.hernandez.mickael.moodtracker.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mickael Hernandez on 23/09/2017.
 */

public class DayMood { // Represents a mood for a specific day, that can be commented

    private Mood mMood;
    private Date mDate;
    private String mComment;

    public DayMood(){ // Constructor by default
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

    public void setDate(Date date) {
        mDate = date;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
