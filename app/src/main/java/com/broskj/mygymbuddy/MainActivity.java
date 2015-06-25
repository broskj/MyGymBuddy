package com.broskj.mygymbuddy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
        workoutsJson = mySharedPreferences.getString(JSON_PREFS_KEY, "");
    }//end declarations

    public void setListViewAdapter() {
        //Toast.makeText(this, "setListViewAdapter called", Toast.LENGTH_SHORT).show();
        adapter = new WorkoutAdapter(this, generateData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }//end setListViewAdapter

    public void loadJson() {
        gson = new Gson();
        workouts = gson.fromJson(workoutsJson, new TypeToken<ArrayList<Workout>>() {
        }.getType());
    }//end loadJson

    public void saveJson() {
        gson = new Gson();
        editor = mySharedPreferences.edit();
        editor.putString(JSON_PREFS_KEY, gson.toJson(workouts));
        editor.apply();
    }

    public void onAddButtonClick(View view) {
        //start activity here to add information
        //test with simple workout
        try {
            System.out.println("onAddButtonClick pressed");
            workouts.add(new Workout("SL 5x5 A"));
        } catch (NullPointerException e) {
            System.out.println("null pointer exception in loadJson()");
        }
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
                models.add(new WorkoutModel(R.color.material_blue, workouts.get(i).name));
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
                Toast.makeText(this, "Settings pushed", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }//end OnOptionsItemSelected

}//end class MainActivity