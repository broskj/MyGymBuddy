package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/25/2015.
 */
public class ExerciseModel {

    public int icon;
    public String title;
    public int type;
    public int whichWorkout;
    //likely needs more - sets/reps, weight, etc. for lifts, time for cardio, increment amount for both

    public ExerciseModel(String _title) {
        this(-1, _title, -1, -1);
    }

    public ExerciseModel(int _icon, String _title, int _type, int _whichWorkout) {
        super();
        this.icon = _icon;
        this.title = _title;
        this.type = _type;
        this.whichWorkout = _whichWorkout;
    }
}
