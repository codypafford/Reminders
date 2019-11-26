package com.example.android_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Random rand = new Random();
    Boolean showDeleteBtn = false;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    Toolbar toolbar;
    Toolbar pop_toolbar;
    ImageButton add_btn;
    ImageButton back_tb_button;

    ImageButton submit_btn;

    int tv_to_delete;

    int count = 0;
    List<String> tvs = new ArrayList<String>();
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "myprefs";
    public static final String value = "key";

    String selDate = "";

    HashMap<Integer, Boolean> highlighted_for_deletion_table = new HashMap<>();

    Button delete_btn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit = sharedpreferences.edit();
        colorTvs();


        // get data from shared prefs
        Gson gson = new Gson();
        String jsonText = sharedpreferences.getString("key", null);
        String[] text = gson.fromJson(jsonText, String[].class);

        System.out.println("------------------------------------------------------------------------------" + Arrays.toString(text));


        // gives access to the other xml layout
        View v = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pop_toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(pop_toolbar);

        add_btn = findViewById(R.id.btn_add);
        add_btn.setOnClickListener(this);

        tvs.addAll(Arrays.asList(text));

        populateReminders();

        for (int i = 0; i < 17; i++) {
            highlighted_for_deletion_table.put(i, false);
        }
        delete_btn = findViewById(R.id.btn_delete);

        delete_btn.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuAbout:
                Toast.makeText(this, "Created by Cody Pafford", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSettings:
                Toast.makeText(this, "You clicked settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLogout:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }


    @Override
    public void onClick(View v) {
        TextView thisTextView;
        String id;
        String[] splitId;

        switch (v.getId()) {

            case R.id.btn_add:
                if (tvs.size() == 17){
                    Toast.makeText(getApplicationContext(), "Too Many Reminders", Toast.LENGTH_LONG).show();
                    return;
                }
                setContentView(R.layout.popupwindow);
                back_tb_button = findViewById(R.id.btn_back);
                back_tb_button.setOnClickListener(this);
                submit_btn = findViewById(R.id.submit_btn);
                submit_btn.setOnClickListener(this);

                CalendarView calendarView = findViewById(R.id.calendarView2);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView arg0, int year, int month,
                                                    int date) {
                        if (month == 12){
                            month = 1;
                        }
                        else{
                            month += 1;
                        }
                        String d = month + "/" + date + "/" + year;
                        selDate = d;
                        Toast.makeText(getApplicationContext(), selDate, Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case R.id.btn_back:
                setContentView(R.layout.activity_main);
                toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("          SET REMINDERS");
                setSupportActionBar(toolbar);

                pop_toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(pop_toolbar);

                add_btn = findViewById(R.id.btn_add);
                add_btn.setOnClickListener(this);

                delete_btn = findViewById(R.id.btn_delete);
                delete_btn.setOnClickListener(this);
                populateReminders();
                colorTvs();
                break;

            case R.id.submit_btn:
                String input;
                Date date = new Date();
                TextInputEditText reminder = findViewById(R.id.edt_reminder);
                if (selDate.isEmpty()) {
                    selDate = formatter.format(date);
                }
                selDate = padRight(selDate, 60);
                input = selDate + "" + reminder.getText().toString();

                //go back to main activity
                setContentView(R.layout.activity_main);
                tvs.add(input);
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor mEdit = sharedpreferences.edit();

                // ADD REMINDERS TO ARRAY LIST AND THEN TO SHARED PREFS UNDER THE KEY -> "key"
                Gson gson = new Gson();
                List<String> textList = new ArrayList<String>(tvs);
                String jsonText = gson.toJson(textList);
                mEdit.putString("key", jsonText);
                mEdit.apply();

                populateReminders();


                toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("          SET REMINDERS");
                setSupportActionBar(toolbar);
                colorTvs();

                pop_toolbar = findViewById(R.id.toolbar2);
                setSupportActionBar(pop_toolbar);

                add_btn = findViewById(R.id.btn_add);
                add_btn.setOnClickListener(this);

                delete_btn = findViewById(R.id.btn_delete);
                delete_btn.setOnClickListener(this);
                selDate = "";

            case R.id.textView1:
                thisTextView = findViewById(R.id.textView1);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                tv_to_delete = 1;
                updateDeletionList(id);
                break;

            case R.id.textView2:
                thisTextView = findViewById(R.id.textView2);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                tv_to_delete = 2;
                updateDeletionList(id);
                break;

            case R.id.textView3:
                thisTextView = findViewById(R.id.textView3);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 3;
                break;

            case R.id.textView4:
                thisTextView = findViewById(R.id.textView4);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 4;

                break;

            case R.id.textView5:
                thisTextView = findViewById(R.id.textView5);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 5;

                break;

            case R.id.textView6:
                thisTextView = findViewById(R.id.textView6);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 6;

                break;

            case R.id.textView7:
                thisTextView = findViewById(R.id.textView7);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 7;
                break;

            case R.id.textView8:
                thisTextView = findViewById(R.id.textView8);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 8;
                break;

            case R.id.textView9:
                thisTextView = findViewById(R.id.textView9);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 9;
                break;

            case R.id.textView10:
                thisTextView = findViewById(R.id.textView10);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 10;
                break;

            case R.id.textView11:
                thisTextView = findViewById(R.id.textView11);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 11;
                break;

            case R.id.textView12:
                thisTextView = findViewById(R.id.textView12);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 12;
                break;

            case R.id.textView13:
                thisTextView = findViewById(R.id.textView13);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
//                Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 13;
                break;

            case R.id.textView14:
                thisTextView = findViewById(R.id.textView14);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
                //  Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
                updateDeletionList(id);
                tv_to_delete = 14;
                break;

            case R.id.textView15:
                thisTextView = findViewById(R.id.textView15);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
                updateDeletionList(id);
                tv_to_delete = 15;
                break;

            case R.id.textView16:
                thisTextView = findViewById(R.id.textView16);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
                updateDeletionList(id);
                tv_to_delete = 16;
                break;

            case R.id.textView17:
                thisTextView = findViewById(R.id.textView17);
                id = thisTextView.getResources().getResourceName(thisTextView.getId());
                splitId = id.split("/");
                id = splitId[1];
                updateDeletionList(id);
                tv_to_delete = 17;
                break;

            case R.id.btn_delete:
                Log.d("why", "*********************************TVS" + tvs);


                tvs.remove(tv_to_delete - 1);

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                mEdit = sharedpreferences.edit();

                // ADD REMINDERS TO ARRAY LIST AND THEN TO SHARED PREFS UNDER THE KEY -> "key"
                gson = new Gson();
                textList = new ArrayList<>(tvs);
                jsonText = gson.toJson(textList);
                mEdit.putString("key", jsonText);
                mEdit.apply();
                populateReminders();
                unselectAllTextViews();
                colorTvs();

                break;


            default:
                break;
        }

    }


    public void populateReminders() {
        LinearLayout linearlayout = (LinearLayout) findViewById(R.id.lin_layout);

        System.out.println("/////////" + tvs);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit = sharedpreferences.edit();


//        // get data from shared prefs
//        Gson gson = new Gson();
//        String jsonText = sharedpreferences.getString("key", null);
//        String[] text = gson.fromJson(jsonText, String[].class);
//        tvs.clear();
//        tvs.addAll(Arrays.asList(text));


        if (tvs.size() == 0) {
            for (int i = 0; i < 17; i++) {
                TextView tv = (TextView) linearlayout.getChildAt(i);
                tv.setText("");
            }
            return;
        }

        for (int i = 0; i < 17; i++) {
            TextView tv = (TextView) linearlayout.getChildAt(i);
            tv.setText("");
        }

        System.out.println(tvs);

        for (int i = 0; i < tvs.size(); i++) {
            TextView tv = (TextView) linearlayout.getChildAt(i);
            tv.setText(tvs.get(i));
        }


    }

    public void updateDeletionList(String view_id) {
        int tvNumber;
        Button deleteButton = (Button) findViewById(R.id.btn_delete);


        int x = getResources().getIdentifier(view_id, "id", getPackageName()); //get id number by string id
        TextView inputView = findViewById(x);
        String input = inputView.getText().toString();
        Log.d("why", input);
        if (input == null || input.isEmpty()) {
            return;
        }

        try {
            tvNumber = Integer.parseInt(view_id.substring(view_id.length() - 2));
        } catch (Exception e) {
            tvNumber = Integer.parseInt(view_id.substring(view_id.length() - 1));
        }


        Boolean isHighlighted = highlighted_for_deletion_table.get(tvNumber);

        isHighlighted = !isHighlighted;
        highlighted_for_deletion_table.put(tvNumber, isHighlighted);

        colorTvs();
        for (int i = 1; i <= 17; i++) {
            String tv_string = "textView" + i;
            int ID = getResources().getIdentifier(tv_string, "id", getPackageName());
            TextView v = findViewById(ID);
            if (i == tvNumber) {
                if (isHighlighted) {
                    v.setBackgroundResource(R.color.colorAccent);
                } else {
                    if (i % 2 == 0) {
                        v.setBackgroundResource(R.color.colorLightPurple);
                    } else {
                        v.setBackgroundResource(R.color.white);
                    }
                }
            } else {
                if (i % 2 == 0) {
                    v.setBackgroundResource(R.color.colorLightPurple);
                } else {
                    v.setBackgroundResource(R.color.white);
                }
            }

        }

        for (int i = 1; i <= 17; i++) {
            if (i == tvNumber) {
                if (isHighlighted) {
                    deleteButton.setVisibility(View.VISIBLE);
                    Log.d("why", "setting to visible");
                    break;
                } else {
                    if (i == 17) {
                        deleteButton.setVisibility(View.INVISIBLE);
                    }
                }
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
            }
        }



    }

    public void unselectAllTextViews() {
        Button deleteButton = (Button) findViewById(R.id.btn_delete);


        for (int i = 0; i < 17; i++) {
            highlighted_for_deletion_table.put(i, false);
        }

        for (int i = 1; i <= 17; i++) {
            String view_id = "textView" + i;
            int x = getResources().getIdentifier(view_id, "id", getPackageName());
            TextView l = findViewById(x);
            l.setBackgroundResource(R.color.white);
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public void colorTvs(){
        for (int i = 1; i <= 17; i++) {
            String tv_string = "textView" + i;
            int ID = getResources().getIdentifier(tv_string, "id", getPackageName());
            TextView v = findViewById(ID);
            if (i % 2 == 0) {
                v.setBackgroundResource(R.color.colorLightPurple);
            } else {
                v.setBackgroundResource(R.color.white);
            }
        }
    }
}
