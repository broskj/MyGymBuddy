package com.broskj.mygymbuddy;

/**
 * Created by Kyle on 6/24/2015.
 */

import java.util.ArrayList;

import android.content.Context;
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

public class WorkoutAdapter extends ArrayAdapter<WorkoutModel> {

    private final Context context;
    private final ArrayList<WorkoutModel> modelsArrayList;

    public WorkoutAdapter(Context context, ArrayList<WorkoutModel> modelsArrayList) {

        super(context, R.layout.workout_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;
        rowView = inflater.inflate(R.layout.workout_item, parent, false);

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
                        Toast.makeText(context, "clicked item " + position, Toast.LENGTH_SHORT).show();
                        //find a way to delete position from arraylist of workouts

                        return true;
                    }
                });

                popupMenu.show();
            }
        });
        titleView.setText(modelsArrayList.get(position).title);

        return rowView;
    }
}