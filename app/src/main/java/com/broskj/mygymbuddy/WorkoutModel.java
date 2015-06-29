package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 */
public class WorkoutModel {

    public int icon;
    public String title;
    public long date;

    public WorkoutModel(String title) {
        this(-1, title, -1);
    }

    public WorkoutModel(int icon, String title, long date) {
        super();
        this.icon = icon;
        this.title = title;
        this.date = date;
    }
}