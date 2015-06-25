package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 */

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WorkoutAdapter extends ArrayAdapter<WorkoutModel> {

    private final Context context;
    private final ArrayList<WorkoutModel> modelsArrayList;
    private Gson gson;
    private SharedPreferences mySharedPreferences;
    private final String JSON_PREFS_KEY = "workoutsJson";
    private final String DELETE = "Are you sure you want to delete this workout?";
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

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, menu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(context, "clicked item " + position, Toast.LENGTH_SHORT).show();
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
                            //edit
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
        titleView.setText(modelsArrayList.get(position).title);

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
            System.out.println("null pointer exception in WorkoutAdapter->generateData()");
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