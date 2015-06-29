package com.broskj.mygymbuddy;

import java.util.ArrayList;

/**
 * Created by Kyle on 6/24/2015.
 * <p/>
 * Intended to manage a list of exercises, and other details
 */
public class Workout {
    String name;
    ArrayList<Exercise> exercises;
    long date;

    Workout() {
        this.name = "";
        this.exercises = new ArrayList<>();
        this.date = -1;
    }//end default constructor

    Workout(String _name) {
        this.name = _name.toUpperCase();
        this.exercises = new ArrayList<>();
        this.date = -1;
    }//end constructor

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void completeWorkout() {
        this.date = System.currentTimeMillis();
    }

}//end class Workout
