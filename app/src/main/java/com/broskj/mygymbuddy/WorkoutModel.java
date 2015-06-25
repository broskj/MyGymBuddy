package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 */
public class WorkoutModel {

    public int icon;
    public String title;

    public WorkoutModel(String title) {
        this(-1, title);
    }

    public WorkoutModel(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }
}