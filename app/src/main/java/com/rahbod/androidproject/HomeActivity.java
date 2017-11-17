package com.rahbod.androidproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {
    private static final int ADD_REQUEST = 100;

    DbHelper dbHelper;
    ListView listView;
    FloatingActionButton fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DbHelper(this);

        fba = (FloatingActionButton) findViewById(R.id.fab);
        fba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddVoiceIntent = new Intent(HomeActivity.this, AddVoiceActivity.class);
                startActivityForResult(AddVoiceIntent, ADD_REQUEST);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        refreshAdapter();
    }

    private void refreshAdapter() {
        SQLiteDatabase db2 = dbHelper.getReadableDatabase();
        Cursor c = db2.rawQuery("SELECT * FROM sounds", null);
        if(c.getCount() > 0){
            List<Sound> data = new ArrayList<>();
            while (c.moveToNext()) {
                Sound sound = new Sound(c.getString(c.getColumnIndex("voice")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("user")));
                data.add(sound);
            }
            c.close();
            SoundAdapter customAdapter = new SoundAdapter(this, data);
            listView.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUEST && resultCode == RESULT_OK)
        {
            refreshAdapter();
            Toast.makeText(this, "List View Refreshed.", Toast.LENGTH_SHORT).show();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}