package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 */

import java.util.ArrayList;

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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WorkoutAdapter extends ArrayAdapter<WorkoutModel> {

    private final Context context;
    private final ArrayList<WorkoutModel> modelsArrayList;
    private final String JSON_PREFS_KEY = "workoutsJson";
    private final String DELETE = "Are you sure you want to delete this workout?";
    private final String RENAME = "Rename Workout";
    private Gson gson;
    private SharedPreferences mySharedPreferences;
    private ArrayList<Workout> workouts;

    public WorkoutAdapter(Context context, ArrayList<WorkoutModel> modelsArrayList) {

        super(context, R.layout.workout_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }//end constructor

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.workout_item, parent, false);

        final Button menu = (Button) rowView.findViewById(R.id.bt_menu);
        TextView titleView = (TextView) rowView.findViewById(R.id.workout_title);
        TextView tvLastSince = (TextView) rowView.findViewById(R.id.tv_time_since);

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
                                    mySharedPreferences = context.getSharedPreferences("preferences", Context.MODE_MULTI_PROCESS);
                                    loadJson();
                                    workouts.remove(position);
                                    saveJson();
                                    //refresh listView to immediately show removed item
                                    refresh(workouts);
                                }
                            }).setNegativeButton("No", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (item.getTitle().equals("Rename")) {
                        /*
                        rename
                         */
                            mySharedPreferences = context.getSharedPreferences("preferences", Context.MODE_MULTI_PROCESS);
                            loadJson();
                            //creates edittext to be used within the dialog and customizes it
                            final EditText input = new EditText(context);
                            input.requestFocus();
                            input.setTextColor(context.getResources().getColor(R.color.black));
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                            input.setText(workouts.get(position).name);
                            input.setSelection(0, workouts.get(position).name.length());

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
                                                workouts.get(position).name = name;
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
        long duration = System.currentTimeMillis() - modelsArrayList.get(position).date;
        if (duration == 0 || duration == -1) {
            tvLastSince.setText("");
        } else if (duration < 60 * DateUtils.SECOND_IN_MILLIS) {
            if (duration / DateUtils.SECOND_IN_MILLIS < 5)
                tvLastSince.setText("Just now");
            else
                tvLastSince.setText(Long.toString(duration / DateUtils.SECOND_IN_MILLIS) + "s");
        } else if (duration < 60 * DateUtils.MINUTE_IN_MILLIS) {
            tvLastSince.setText(Long.toString(duration / DateUtils.MINUTE_IN_MILLIS) + "m");
        } else if (duration < 24 * DateUtils.HOUR_IN_MILLIS) {
            tvLastSince.setText(Long.toString(duration / DateUtils.HOUR_IN_MILLIS) + "h");
        } else if (duration < 7 * DateUtils.DAY_IN_MILLIS) {
            tvLastSince.setText(Long.toString(duration / DateUtils.DAY_IN_MILLIS) + "d");
        } else if (duration < 4 * DateUtils.WEEK_IN_MILLIS) {
            tvLastSince.setText(Long.toString(duration / DateUtils.WEEK_IN_MILLIS) + "w");
        } else if (duration < 4 * 12 * DateUtils.WEEK_IN_MILLIS/*less than one year*/) {
            tvLastSince.setText(Long.toString(duration / (12 * DateUtils.WEEK_IN_MILLIS)) + "m");
        } else
            tvLastSince.setText("");

        return rowView;
    }//end getView

    public void refresh(ArrayList<Workout> workouts) {
        ArrayList<WorkoutModel> models = new ArrayList<>();
        try {
            for (int i = 0; i < workouts.size(); i++) {
                System.out.println("workout " + i + ": " + workouts.get(i).name);
                models.add(new WorkoutModel(workouts.get(i).name));
            }
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in WorkoutAdapter->refresh()");
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
            if (temp != null)
                workouts = temp;
        } catch (Exception e) {
            workouts = new ArrayList<>();
            System.out.println("exception in WorkoutAdapter->loadJson()");
        }
    }//end loadJson

    public void saveJson() {
        gson = new Gson();
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(JSON_PREFS_KEY, gson.toJson(workouts));
        System.out.println("workouts json: " + gson.toJson(workouts));
        editor.apply();
    }
}// end class WorkoutAdapter