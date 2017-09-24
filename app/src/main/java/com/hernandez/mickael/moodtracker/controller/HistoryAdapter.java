package com.hernandez.mickael.moodtracker.controller;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hernandez.mickael.moodtracker.R;
import com.hernandez.mickael.moodtracker.model.DayMood;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Mickael Hernandez on 24/09/2017.
 */

public class HistoryAdapter extends ArrayAdapter<DayMood> {

    private final Context context;
    private final ArrayList<DayMood> itemsArrayList;

    /*public HistoryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }*/

    public HistoryAdapter(Context context, ArrayList<DayMood> items) {
        super(context, R.layout.component_history_row, items);
        this.context = context;
        this.itemsArrayList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.component_history_row, parent, false);
        }

        final DayMood dm = getItem(position);
        Log.d("mood_date", dm.getDate().toString());
        Log.d("mood_mood", dm.getMood().toString());
        if (dm != null) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), dm.getMood().getColor()));
            Date today = Calendar.getInstance().getTime();
            TextView text = (TextView) v.findViewById(R.id.row_textview);
            ImageView img = (ImageView) v.findViewById(R.id.row_imageview);

            if (text != null) {
                long daysAgo = DAYS.convert(today.getTime() - dm.getDate().getTime(), MILLISECONDS);
                String str = String.format(getContext().getResources().getString(R.string.history_row_text), daysAgo);
                text.setText(str);
            }

            if (img != null && !dm.getComment().equals("")) { // if there's a comment, show the icon and a toast on click
                img.setVisibility(View.VISIBLE);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), dm.getComment(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return v;
    }

}
