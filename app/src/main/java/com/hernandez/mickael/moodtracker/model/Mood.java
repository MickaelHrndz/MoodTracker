package com.hernandez.mickael.moodtracker.model;

import com.hernandez.mickael.moodtracker.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mickael Hernandez on 22/09/2017.
 */

public enum Mood {

    SAD(0L, R.color.faded_red),
    DISAPPOINTED(1L, R.color.warm_grey),
    NORMAL(2L, R.color.cornflower_blue_65),
    HAPPY(3L, R.color.light_sage),
    SUPERHAPPY(4L, R.color.banana_yellow),
    UNKNOWN(-1L, R.color.faded_red);

    private static final Map<Long, Mood> byId = new HashMap<Long, Mood>();
    static {
        for (Mood e : Mood.values()) {
            if (byId.put(e.getId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    public static Mood getById(Long id) {
        return byId.get(id);
    }
    
    private Long id = 0L;

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
