package com.broskj.mygymbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Kyle on 6/24/2015.
 * <p/>
 * Main page, displays custom workouts in a listview
 */

public class MainActivity extends Activity {
    final String JSON_PREFS_KEY = "workoutsJson";
    final String ADD_WORKOUT = "NAME YOUR WORKOUT";
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    String workoutsJson;
    WorkoutAdapter adapter;
    ListView listView;
    ArrayList<Workout> workouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declarations();
        loadJson();
        setListViewAdapter();
    }

    public void declarations() {
        workouts = new ArrayList<>();
        listView = (ListView) findViewById(R.id.lv_main);
        mySharedPreferences = getSharedPreferences("preferences", MODE_MULTI_PROCESS);
    }//end declarations

    public void setListViewAdapter() {
        //Toast.makeText(this, "setListViewAdapter called", Toast.LENGTH_SHORT).show();
        adapter = new WorkoutAdapter(this, generateData());
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.lv_empty_workouts));
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("listview item clicked");
                Toast.makeText(MainActivity.this, "position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }//end setListViewAdapter

    public void loadJson() {
        gson = new Gson();
        workoutsJson = mySharedPreferences.getString(JSON_PREFS_KEY, "");
        try {
            //Toast.makeText(this, "loadJson called", Toast.LENGTH_SHORT).show();
            System.out.println("workoutsJson: " + workoutsJson);
            ArrayList<Workout> temp = gson.fromJson(workoutsJson, new TypeToken<ArrayList<Workout>>() {
            }.getType());
            if (temp != null)
                workouts = temp;
        } catch (Exception e) {
            workouts = new ArrayList<>();
            System.out.println("exception in loadJson()");
        }
    }//end loadJson

    public void saveJson() {
        gson = new Gson();
        editor = mySharedPreferences.edit();
        editor.putString(JSON_PREFS_KEY, gson.toJson(workouts));
        editor.apply();
    }

    public void onAddButtonClick(View view) {
        //start activity or fragment here to add information
        //test with simple workout
        loadJson();
        try {
            System.out.println("onAddButtonClick pressed");
            final EditText input = new EditText(this);
            input.requestFocus();
            input.setTextColor(getResources().getColor(R.color.black));
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(input);
            builder.setTitle(ADD_WORKOUT).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    if (name.equals("")) {
                        input.requestFocus();
                        input.setError("WORKOUT NEEDS TITLE");
                    } else {
                        addWorkout(name);
                    }
                }
            }).setNegativeButton("CANCEL", null);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.show();
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in onAddButtonClick()");
        }
    }

    public void addWorkout(String name) {
        workouts.add(new Workout(name));
        setListViewAdapter();
        saveJson();
    }

    private ArrayList<WorkoutModel> generateData() {
        /*
        populates the ListView on activity_main.xml with an icon and title.
         */
        ArrayList<WorkoutModel> models = new ArrayList<>();
        try {
            for (int i = 0; i < workouts.size(); i++) {
                models.add(new WorkoutModel(workouts.get(i).name));
            }
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in generateData()");
        }

        return models;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }//end OnCreateOptionsMenu

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }//end onKeyDown

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                //Toast.makeText(this, "Settings pushed", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }//end OnOptionsItemSelected

}//end class MainActivity