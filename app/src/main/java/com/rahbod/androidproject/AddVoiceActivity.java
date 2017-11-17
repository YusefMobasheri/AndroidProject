package com.rahbod.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddVoiceActivity extends AppCompatActivity {
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voice);
        dbHelper = new DbHelper(this);


    }
}
