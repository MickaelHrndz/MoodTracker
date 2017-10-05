package com.hernandez.mickael.moodtracker.model;

import com.hernandez.mickael.moodtracker.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mickael Hernandez on 22/09/2017.
 */

/** Enumeration of the displayed moods */
public enum Mood {

    /** Mood values (id, color) */
    SAD(0L, R.color.faded_red),
    DISAPPOINTED(1L, R.color.warm_grey),
    NORMAL(2L, R.color.cornflower_blue_65),
    HAPPY(3L, R.color.light_sage),
    SUPER_HAPPY(4L, R.color.banana_yellow),
    UNKNOWN(-1L, R.color.faded_red);

    /** Mood by default (the normal one) */
    public static final int DEFAULT_MOOD = 2;

    /** Map used to get a mood by its id */
    private static final Map<Long, Mood> byId = new HashMap<>();

    static { // static block to fill the byId map
        for (Mood e : Mood.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    /** Returns mood corresponding to the id */
    public static Mood getById(Long id) {
        return byId.get(id);
    }

    /** Mood id */
    private Long id = 0L;

    /** Mood color */
    private int color;

    Mood(Long pId, int color) {
        this.id = pId;
        this.color = color;
    }

    public Long getId(){
        return id;
    }

    public int getColor(){
        return color;
    }
}
