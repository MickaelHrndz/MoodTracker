package com.hernandez.mickael.moodtracker.model;

import com.hernandez.mickael.moodtracker.R;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Mickaël Hernandez on 30/09/2017.
 */
public class MoodTest {
    @Test
    public void getById() throws Exception {
        long input = 4; // mock input
        Mood expected = Mood.SUPER_HAPPY; // expected value

        Mood output = Mood.getById(input);

        assertEquals(expected, output);
    }

    @Test
    public void getId() throws Exception {
        Mood input = Mood.HAPPY;
        long expected = 3;

        long output = input.getId();

        assertEquals(expected, output);
    }

    @Test
    public void getColor() throws Exception {
        Mood input = Mood.NORMAL;
        int expected = R.color.cornflower_blue_65;

        int output = input.getColor();

        assertEquals(expected, output);
    }

}