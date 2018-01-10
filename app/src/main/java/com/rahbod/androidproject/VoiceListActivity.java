package com.rahbod.androidproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VoiceListActivity extends AppCompatActivity {
    private static final int ADD_REQUEST = 100;

    SQLiteDatabase db;
    ListView listView;
    SoundAdapter customAdapter = null;
    FloatingActionButton fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_list);

        db = (new DbHelper(this)).getReadableDatabase();

        fba = (FloatingActionButton) findViewById(R.id.fab);
        fba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent AddVoiceIntent = new Intent(VoiceListActivity.this, AddActivity.class);
            startActivityForResult(AddVoiceIntent, ADD_REQUEST);
            }
        });


        listView = (ListView) findViewById(R.id.listView);
        Cursor c = db.rawQuery("SELECT * FROM sounds", null);
        if(c.getCount() > 0){
            List<Sound> data = new ArrayList<>();
            while (c.moveToNext()) {
                Sound sound = new Sound(c.getInt(c.getColumnIndex("id")), c.getBlob(c.getColumnIndex("voice")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("username")));
                data.add(sound);
            }
            c.close();
            customAdapter = new SoundAdapter(this, data);
            listView.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REQUEST && resultCode == RESULT_OK)
        {
            Cursor c = db.rawQuery("SELECT * FROM sounds", null);
            if(c.getCount() > 0) {
                List<Sound> list = new ArrayList<>();
                while (c.moveToNext()) {
                    Sound sound = new Sound(c.getInt(c.getColumnIndex("id")), c.getBlob(c.getColumnIndex("voice")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("username")));
                    list.add(sound);
                }
                c.close();
                if (customAdapter != null) {
                    customAdapter.refreshSounds(list);
                    customAdapter.notifyDataSetChanged();
                }else{
                    customAdapter = new SoundAdapter(this, list);
                    listView.setAdapter(customAdapter);
                }
            }
            Toast.makeText(this, "List View Refreshed.", Toast.LENGTH_SHORT).show();
        }
    }
}
