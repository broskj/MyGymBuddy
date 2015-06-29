package com.broskj.mygymbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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
    final String ADD_WORKOUT = "New Workout";
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

    @Override
    protected void onResume() {
        super.onResume();
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
                System.out.println("MainActivity->listview item " + position + " clicked");
                startActivity(new Intent(MainActivity.this, ExerciseActivity.class).putExtra("position", position).putExtra("workoutsJson", workoutsJson));
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
        loadJson();
        try {
            System.out.println("onAddButtonClick pressed");
            //creates edittext to be used within the dialog and customizes it
            final EditText input = new EditText(this);
            input.requestFocus();
            input.setTextColor(getResources().getColor(R.color.black));
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

            //creates the dialog builder, sets the view to the edittext, and adds buttons
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(input);
            builder.setTitle(ADD_WORKOUT).setPositiveButton("OK", null).setNegativeButton("Cancel", null);

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
                                input.setError("Enter title");
                            } else {
                                addWorkout(name);
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
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
        loadJson();
    }//end addWorkout

    private ArrayList<WorkoutModel> generateData() {
        /*
        populates the ListView on activity_main.xml with an icon and title.
         */
        ArrayList<WorkoutModel> models = new ArrayList<>();
        try {
            for (int i = 0; i < workouts.size(); i++) {
                models.add(new WorkoutModel(-1, workouts.get(i).name, workouts.get(i).date));
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

    public void onEmptyClick(View view) {
        onAddButtonClick(view);
    }
}//end class MainActivity