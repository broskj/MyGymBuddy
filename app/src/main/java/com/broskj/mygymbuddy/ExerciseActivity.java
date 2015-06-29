package com.broskj.mygymbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Kyle on 6/25/2015.
 */
public class ExerciseActivity extends Activity {
    final String JSON_PREFS_KEY = "workoutsJson";
    int position;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    String workoutsJson, finishDialog;
    ExerciseAdapter adapter;
    ListView listView;
    ArrayList<Workout> workouts;
    ArrayList<Exercise> exercises;
    Workout currentWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        declarations();
        setListViewAdapter();

        getActionBar().setTitle(currentWorkout.name);
    }//end onCreate


    public void declarations() {
        workouts = new ArrayList<>();
        position = getIntent().getExtras().getInt("position");
        workoutsJson = getIntent().getExtras().getString("workoutsJson");
        loadJson();
        currentWorkout = workouts.get(position);
        exercises = currentWorkout.exercises;
        listView = (ListView) findViewById(R.id.lv_exercises);
        mySharedPreferences = getSharedPreferences("preferences", MODE_MULTI_PROCESS);
    }//end declarations

    public void setListViewAdapter() {
        adapter = new ExerciseAdapter(this, generateData());
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.lv_empty_exercises));
    }//end setListViewAdapter

    public void loadJson() {
        gson = new Gson();
        try {
            //Toast.makeText(this, "loadJson called", Toast.LENGTH_SHORT).show();
            //System.out.println("workoutsJson: " + workoutsJson);
            ArrayList<Workout> temp = gson.fromJson(workoutsJson, new TypeToken<ArrayList<Workout>>() {
            }.getType());
            if (temp != null)
                workouts = temp;
        } catch (Exception e) {
            workouts = new ArrayList<>();
            System.out.println("exception in ExerciseActivity->loadJson()");
        }
    }//end loadJson

    public void saveJson() {
        gson = new Gson();
        editor = mySharedPreferences.edit();
        editor.putString(JSON_PREFS_KEY, gson.toJson(workouts));
        editor.apply();
    }

    private ArrayList<ExerciseModel> generateData() {
        /*
        populates the ListView on activity_main.xml with an icon, a title, and an exercise type
         */
        ArrayList<ExerciseModel> models = new ArrayList<>();
        try {
            for (int i = 0; i < exercises.size(); i++) {
                if (exercises.get(i).getType() == 0)
                    models.add(new ExerciseModel(R.drawable.runner, exercises.get(i).getName(), 0, position));
                else if (exercises.get(i).getType() == 1)
                    models.add(new ExerciseModel(R.drawable.dumbell, exercises.get(i).getName(), 1, position));
                else
                    System.out.println("Something happened ExerciseActivity->generateData()");
            }
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in ExerciseActivity->generateData()");
        }

        return models;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_exercises, menu);
        return true;
    }//end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_exercise:
                addExercise();
                break;
            case R.id.action_settings:
                //open settings
                break;
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected

    public void onFinishButtonClick(View view) {
        //save date to current exercise, finish activity
        //create dialog: Are you sure you want to finish your workout?
        //or: Your workout doesn't appear to be complete.  Are you sure you want to finish your workout?
        finishDialog = "Are you sure you want to finish your workout?";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(finishDialog).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentWorkout.completeWorkout();
                workouts.set(position, currentWorkout);
                refresh();
                exitActivity();
            }
        }).setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }//end onFinishButtonClick

    public void onEmptyClick(View view) {
        addExercise();
    }//end onEmptyClick

    public void addExercise() {
        //Toast.makeText(this, "Add exercise clicked", Toast.LENGTH_SHORT).show();
        //open activity to add exercise, and add it to current workout
        currentWorkout.addExercise(new Exercise("exercise", false, 0, 3600, 0));
        workouts.set(position, currentWorkout);
        refresh();
    }//end addExercise

    public void refresh() {
        setListViewAdapter();
        saveJson();
        loadJson();
    }//end refresh

    public void exitActivity() {
        this.finish();
    }

}//end class ExerciseActivity
