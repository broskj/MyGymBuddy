package com.broskj.mygymbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
                //increment time/weight
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle("New Exercise").setView(inflater.inflate(R.layout.add_exercise, null))
                .setPositiveButton("Next", null).setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(Dialog.BUTTON_POSITIVE);
                final RadioGroup group = (RadioGroup) dialog.findViewById(R.id.rg_exercise_type);
                final RadioButton rbCardio = (RadioButton) dialog.findViewById(R.id.rb_type_cardio),
                        rbWeightlifting = (RadioButton) dialog.findViewById(R.id.rb_type_weightlifting);

                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (group.getCheckedRadioButtonId() == rbCardio.getId())
                            rbWeightlifting.setChecked(false);
                        else if (group.getCheckedRadioButtonId() == rbWeightlifting.getId())
                            rbCardio.setChecked(false);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText input = (EditText) dialog.findViewById(R.id.et_exercise_title);
                        final String title = input.getText().toString();

                        if (title.equals("")) {
                            input.requestFocus();
                            input.setError("EXERCISE NEEDS TITLE");
                        } else {
                            switch (group.getCheckedRadioButtonId()) {
                                case R.id.rb_type_cardio:
                                    manageCardio(title);
                                    //currentWorkout.addExercise(new Exercise(title, increment, 0, time, incrementTime));
                                    //workouts.set(position, currentWorkout);
                                    //refresh();
                                    break;
                                case R.id.rb_type_weightlifting:
                                    manageWeightlifting(false, title);
                                    //currentWorkout.addExercise(title, increment, repScheme, weightScheme, 1, sets, reps, weight, incrementWeight);
                                    //workouts.set(position, currentWorkout);
                                    //refresh();
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }//end addExercise

    public void manageCardio(final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle(title.toUpperCase()).setView(inflater.inflate(R.layout.add_exercise_0, null))
                .setPositiveButton("Finish", null).setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final RadioGroup group = (RadioGroup) dialog.findViewById(R.id.rg_cardio_interval);
                final RadioButton rbNone = (RadioButton) dialog.findViewById(R.id.rb_cardio_interval_none),
                        rbEach = (RadioButton) dialog.findViewById(R.id.rb_cardio_interval_each);
                final EditText durationHrs = (EditText) dialog.findViewById(R.id.et_duration_hrs),
                        durationMins = (EditText) dialog.findViewById(R.id.et_duration_mins),
                        incrementMins = (EditText) dialog.findViewById(R.id.et_increment_mins);

                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == rbNone.getId()) {
                            rbEach.setChecked(false);
                            incrementMins.setEnabled(false);
                        } else if (checkedId == rbEach.getId()) {
                            rbNone.setChecked(false);
                            incrementMins.setEnabled(true);
                        }
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String dHrs = durationHrs.getText().toString(),
                                dMins = durationMins.getText().toString(),
                                iMins = incrementMins.getText().toString();

                        if (dHrs.equals("")) {
                            durationHrs.requestFocus();
                            durationHrs.setError("NEEDS TIME");
                        } else if (dMins.equals("")) {
                            durationMins.requestFocus();
                            durationMins.setError("NEEDS TIME");
                        } else if (rbEach.isChecked() && iMins.equals("")) {
                            incrementMins.requestFocus();
                            incrementMins.setError("NEEDS TIME");
                        } else {
                            boolean increment;
                            int time, incrementTime;

                            if (rbEach.isChecked()) {
                                increment = true;
                                time = (Integer.parseInt(dHrs) * 60) + Integer.parseInt(dMins);
                                incrementTime = Integer.parseInt(iMins);
                            } else {
                                increment = false;
                                time = (Integer.parseInt(dHrs) * 60) + Integer.parseInt(dMins);
                                incrementTime = 0;
                            }
                            currentWorkout.addExercise(new Exercise(title, increment, 0, time, incrementTime));

                            refresh();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }//end manageCardio

    public void manageWeightlifting(final boolean edit, final String title) {
        if (edit) {
            boolean increment = currentWorkout.exercises.get(position).increment,
                    weightScheme = currentWorkout.exercises.get(position).weightScheme,
                    repScheme = currentWorkout.exercises.get(position).repScheme;
            int sets = currentWorkout.exercises.get(position).sets;
            int[] reps = currentWorkout.exercises.get(position).reps;
            double[] weight = currentWorkout.exercises.get(position).weight;
            double incrementWeight = currentWorkout.exercises.get(position).incrementWeight;
        }

    }//end manageWeightlifting

    public void refresh() {
        saveJson();
        loadJson();
        setListViewAdapter();
    }//end refresh

    public void exitActivity() {
        this.finish();
    }

}//end class ExerciseActivity
