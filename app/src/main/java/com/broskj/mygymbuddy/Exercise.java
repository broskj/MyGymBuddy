package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 * <p/>
 * Intended to contain details about each workout, including type of workout (cardio vs
 * weightlifting) and customizations for each including number of sets and reps for weights and
 * time duration for cardio.
 */

public class Exercise {
    public final int TYPE_CARDIO = 0;
    public final int TYPE_LIFT = 1;
    String name; //exercise name, e.g. 'Leg Day' or '5x5 B'
    boolean increment; //whether or not the user wants to increment weight/time each workout
    boolean repScheme; //whether or not the user is following a rep scheme (10-8-6)
    boolean weightScheme; //whether or not the user is following a weight scheme (135-155-185)
    int type; //either 0 or 1, maybe more in the future
    int time; //measured in seconds, converted into hours:minutes;seconds
    int incrementTime; //measured in seconds, but typically used for adding minutes to 'time'
    int sets; //number of sets per workout
    int[] reps; //number of reps per set.  size initialized to sets
    double weight[]; //amount of initial weight, incrementable
    double incrementWeight; //amount of weight to be added after each successful lift

    Exercise() {
        this.name = "";
    }//end default constructor

    Exercise(String _name, boolean _increment, int _type, int _time, int _incrementTime) {
        this.name = _name.toUpperCase();
        this.increment = _increment;
        this.type = _type;
        this.time = _time;
        this.incrementTime = _incrementTime;
    }//end cardio constructor

    Exercise(String _name, boolean _increment, boolean _repScheme, boolean _weightScheme, int _type, int _sets, int[] _reps,
             double _weight[], double _incrementWeight) {
        this.name = _name.toUpperCase();
        this.increment = _increment;
        this.repScheme = _repScheme;
        this.weightScheme = _weightScheme;
        this.type = _type;
        this.sets = _sets;
        this.reps = _reps;
        this.weight = _weight;
        this.incrementWeight = _incrementWeight;
    }//end weightlifting constructor

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public void incrementWeight() {
        if (increment) {
            weight[0] += incrementWeight;
            for (int i = 1; i < weight.length; i++)
                weight[i] += incrementWeight;
        }
    }//end incrementWeight

    public void incrementTime() {
        if (increment)
            time += incrementTime;
    }

}//end class Exercise
