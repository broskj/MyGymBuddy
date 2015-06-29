package com.broskj.mygymbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

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

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, menu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

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
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        titleView.setText(modelsArrayList.get(position).title);

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
