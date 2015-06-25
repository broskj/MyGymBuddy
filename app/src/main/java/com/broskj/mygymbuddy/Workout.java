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

    Workout() {
        this.name = "";
    }//end default constructor

    Workout(String _name) {
        this.name = _name.toUpperCase();
    }//end constructor

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

}//end class Workout
