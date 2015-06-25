package com.broskj.mygymbuddy;

import javax.xml.datatype.Duration;

/**
 * Created by Kyle on 6/24/2015.
 * <p/>
 * Intended to contain details about each workout, including type of workout (cardio vs
 * weightlifting) and customizations for each including number of sets and reps for weights and
 * time duration for cardio.
 */

public class Exercise {
    final int TYPE_CARDIO = 0;
    final int TYPE_LIFT = 1;
    String name; //exercise name, e.g. 'Leg Day' or '5x5 B'
    boolean increment; //whether or not the user wants to increment weight/time each workout
    int type; //either 0 or 1, maybe more in the future
    int time; //measured in seconds, converted into hours:minutes;seconds
    int incrementTime; //measured in seconds, but typically used for adding minutes to 'time'
    int sets; //number of sets per workout
    int[] reps; //number of reps per set.  size initialized to sets, and
    double weight; //amount of initial weight, incrementable
    double incrementWeight; //amount of weight to be added after each successful lift

    Exercise() {

    }//end default constructor

    Exercise(String _name, boolean _increment, int _type, int _time, int _sets, int[] _reps,
             double _weight, int _incrementTime, double _incrementWeight) {
        this.name = _name.toUpperCase();
        this.increment = _increment;
        this.type = _type;
        this.time = _time;
        this.sets = _sets;
        this.reps = _reps;
        this.weight = _weight;
        this.incrementTime = _incrementTime;
        this.incrementWeight = _incrementWeight;
    }//end constructor

    public void incrementWeight() {
        weight += incrementWeight;
    }//end incrementWeight

    public void incrementTime() {
        time += incrementTime;
    }

}//end class Exercise
