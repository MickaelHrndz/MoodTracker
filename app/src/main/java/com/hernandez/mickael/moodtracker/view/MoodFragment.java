package com.hernandez.mickael.moodtracker.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hernandez.mickael.moodtracker.R;

/**
 * Created by Mickael Hernandez on 22/09/2017.
 */

public class MoodFragment extends Fragment {

    public static MoodFragment newInstance(int num) {
        Bundle arguments = new Bundle();
        arguments.putInt("mood", num);
        MoodFragment fragment = new MoodFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = R.layout.fragment_mood_sad;
        switch(getArguments().getInt("mood")){
            case 0:
                layoutId = R.layout.fragment_mood_sad;
                break;
            case 1:
                layoutId = R.layout.fragment_mood_disappointed;
                break;
            case 2:
                layoutId = R.layout.fragment_mood_normal;
                break;
            case 3:
                layoutId = R.layout.fragment_mood_happy;
                break;
            case 4:
                layoutId = R.layout.fragment_mood_superhappy;
                break;
        }
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                layoutId, container, false);
        return rootView;
    }
}
