package com.broskj.mygymbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kyle on 6/25/2015.
 */
public class ExerciseAdapter extends ArrayAdapter<ExerciseModel> {

    private final Context context;
    private final ArrayList<ExerciseModel> modelsArrayList;
    private final String DELETE = "Are you sure you want to delete this exercise?";
    private final String RENAME = "Rename Exercise";
    private final String JSON_PREFS_KEY = "workoutsJson";
    private Gson gson;
    private SharedPreferences mySharedPreferences;
    private ArrayList<Workout> workouts;
    private Workout currentWorkout;
    private int currentWorkoutIndex;
    private Counter counter;

    public ExerciseAdapter(Context _context, ArrayList<ExerciseModel> _modelsArrayList) {
        super(_context, -1, _modelsArrayList);

        this.context = _context;
        this.modelsArrayList = _modelsArrayList;
    }//end constructor

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mySharedPreferences = context.getSharedPreferences("preferences", Context.MODE_MULTI_PROCESS);
        loadJson();
        currentWorkoutIndex = modelsArrayList.get(position).whichWorkout;
        currentWorkout = workouts.get(currentWorkoutIndex);

        final Button menu;

        View view = convertView;
        if (view == null) {
            /*
            set layout of listView items based on exercise type
             */
            switch (currentWorkout.exercises.get(position).type) {
                case 0: //cardio
                    view = inflater.inflate(R.layout.cardio_item, parent, false);
                    break;
                case 1: //lift
                    view = inflater.inflate(R.layout.lift_item, parent, false);
                    break;
            }
        }

        switch (currentWorkout.exercises.get(position).type) {
            /*
            initializes menu button in listview item
             */
            case 0: //cardio
                menu = (Button) view.findViewById(R.id.bt_cardio_menu);
                break;
            case 1: //lift
                menu = (Button) view.findViewById(R.id.bt_lift_menu);
                break;
            default:
                menu = (Button) view.findViewById(R.id.bt_menu);
        }

        TextView titleView = (TextView) view.findViewById(R.id.exercise_title);
        TextView timerView = (TextView) view.findViewById(R.id.tv_timer);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, menu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_exercises, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        /*
                        delete
                         */
                        if (item.getTitle().equals("Delete")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(DELETE).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currentWorkout.exercises.remove(position);
                                    workouts.set(currentWorkoutIndex, currentWorkout);
                                    saveJson();
                                    //refresh listView to immediately show removed item
                                    refresh(workouts);
                                }
                            }).setNegativeButton("No", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        /*
                        rename
                         */
                        else if (item.getTitle().equals("Rename")) {
                            //creates edittext to be used within the dialog and customizes it
                            //need to consider using a layoutinflater to inflate a dialog with an xml layout instead
                            final EditText input = new EditText(context);
                            input.requestFocus();
                            input.setTextColor(context.getResources().getColor(R.color.black));
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                            input.setText(currentWorkout.exercises.get(position).name);
                            input.setSelection(0, currentWorkout.exercises.get(position).name.length());

                            //creates the dialog builder, sets the view to the edittext, and adds buttons
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setView(input);
                            builder.setTitle(RENAME).setPositiveButton("OK", null).setNegativeButton("Cancel", null);

                            /*
                            creates dialog from builder, creates a listener for the dialog showing, identifies the
                              positive button, and creates an onclicklistener for it.  this allows the dialog to
                              remain open should the edittext be empty upon clicking OK.
                             */
                            final AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface d) {
                                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String name = input.getText().toString();
                                            if (name.equals("")) {
                                                input.requestFocus();
                                                input.setError("WORKOUT NEEDS TITLE");
                                            } else {
                                                currentWorkout.exercises.get(position).name = name;
                                                workouts.set(currentWorkoutIndex, currentWorkout);
                                                saveJson();
                                                //refresh listView to immediately show removed item
                                                refresh(workouts);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            dialog.show();
                        }
                        /*
                        edit
                         */
                        else if (item.getTitle().equals("Edit")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);

                            final String title = currentWorkout.exercises.get(currentWorkoutIndex).getName();

                            builder.setTitle(title).setView(inflater.inflate(R.layout.add_exercise_0, null))
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

                                    final boolean cIncrement = currentWorkout.exercises.get(position).increment;
                                    final long cTime = currentWorkout.exercises.get(position).time;
                                    final long cIncrementTime = currentWorkout.exercises.get(position).incrementTime;

                                    //sets increment buttons based on user preferences
                                    if (cIncrement) {
                                        rbEach.setChecked(true);
                                        rbNone.setChecked(false);
                                        incrementMins.setEnabled(true);
                                    } else {
                                        rbEach.setChecked(false);
                                        rbNone.setChecked(true);
                                        incrementMins.setEnabled(false);
                                    }

                                    durationHrs.setText(Long.toString(TimeUnit.MILLISECONDS.toHours(cTime)));
                                    durationHrs.requestFocus();
                                    durationHrs.setSelection(0, durationHrs.getText().toString().length());
                                    durationMins.setText(Long.toString(TimeUnit.MILLISECONDS.toMinutes(cTime) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(cTime))));
                                    if (cIncrementTime != 0)
                                        incrementMins.setText(Long.toString(TimeUnit.MILLISECONDS.toMinutes(cIncrementTime)));

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
                                            } else if (Integer.parseInt(dMins) >= 60) {
                                                durationMins.requestFocus();
                                                durationMins.setError("TOO MANY MINUTES");
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

                                                Exercise temp = new Exercise(title, increment, 0, time, incrementTime);
                                                currentWorkout.exercises.set(position, temp);

                                                saveJson();
                                                refresh(workouts);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            dialog.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        titleView.setText(modelsArrayList.get(position).title);
        counter = new Counter(timerView, currentWorkout.exercises.get(currentWorkoutIndex).time, 1000);
        timerView.setText(counter.formatTime(currentWorkout.exercises.get(currentWorkoutIndex).time));

        return view;
    }//end getView

    public void refresh(ArrayList<Workout> workouts) {
        ArrayList<ExerciseModel> models = new ArrayList<>();
        currentWorkout = workouts.get(currentWorkoutIndex);
        try {
            for (int i = 0; i < currentWorkout.exercises.size(); i++) {
                System.out.println("exercise " + i + ": " + currentWorkout.exercises.get(i).name);
                models.add(new ExerciseModel(-1, currentWorkout.exercises.get(i).name,
                        currentWorkout.exercises.get(i).getType(), currentWorkoutIndex));
            }
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in ExerciseAdapter->refresh()");
        }
        modelsArrayList.clear();
        modelsArrayList.addAll(models);
        this.notifyDataSetChanged();
    }

    public void loadJson() {
        gson = new Gson();
        String workoutsJson = mySharedPreferences.getString(JSON_PREFS_KEY, "");
        try {
            ArrayList<Workout> temp = gson.fromJson(workoutsJson, new TypeToken<ArrayList<Workout>>() {
            }.getType());
            if (temp != null) {
                workouts = temp;
                currentWorkout = workouts.get(currentWorkoutIndex);
            }
        } catch (Exception e) {
            workouts = new ArrayList<>();
            currentWorkout = new Workout();
            System.out.println("exception in WorkoutAdapter->loadJson()");
        }
    }//end loadJson

    public void saveJson() {
        gson = new Gson();
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(JSON_PREFS_KEY, gson.toJson(workouts));
        //System.out.println("workouts json: " + gson.toJson(workouts));
        editor.apply();
    }

    @Override
    public int getItemViewType(int position) {
        return modelsArrayList.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}//end class ExerciseAdapter
