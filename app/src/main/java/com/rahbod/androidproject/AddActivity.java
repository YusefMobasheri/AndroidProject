package com.rahbod.androidproject;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private static RecorderThread recorderThread = null;
    ImageButton imageButton;
    static ImageButton imagePlay;
    Button btnSave;
    EditText etTitle;
    SQLiteDatabase db;
    
    boolean recordState = false;
    static boolean playState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = openOrCreateDatabase("MYDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sounds(id integer primary key autoincrement, voice blob, " +
                "title VARCHAR(100), username VARCHAR(50));");

        etTitle = (EditText) findViewById(R.id.etTitle);

        imageButton = (ImageButton) findViewById(R.id.btnRecord);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordState)
                    stopRecordingBtn();
                else
                    startRecording();
            }
        });

        imagePlay = (ImageButton) findViewById(R.id.imagePlay);
        imagePlay.setEnabled(false);
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordState)
                    stopRecordingBtn();
                if (playState)
                    stopPlayingBtn();
                else
                    startPlaying();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.getText().toString().equals(""))
                    Toast.makeText(AddActivity.this, "Title can not be empty.", Toast.LENGTH_SHORT).show();
                else if (recorderThread == null || !recorderThread.hasStream())
                    Toast.makeText(AddActivity.this, "Please first record a voice.", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        ContentValues values = new ContentValues();
                        values.put("title", etTitle.getText().toString());
                        values.put("voice", recorderThread.getStream());
                        values.put("username", "yusef");
                        db.insert("sounds", null, values);
                        setResult(Activity.RESULT_OK);
                    } catch (Exception e) {
                        Log.e("e", e.getMessage());
                        setResult(Activity.RESULT_CANCELED);
                    }
                    finish();
                }
            }
        });
    }


    private void startRecording() {

        try {
            stopRecordingBtn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorderThread = new RecorderThread();
        recorderThread.mContext = this;
        recordState = true;
        recorderThread.mRecordContinue = true;
        recorderThread.recordAudio();
        Toast.makeText(AddActivity.this, "Start Recording...", Toast.LENGTH_SHORT).show();
        imageButton.setImageResource(R.drawable.ic_mic_red_24dp);

    }

    private void stopRecordingBtn() {
        if (recorderThread != null && !recorderThread.recordThread.isInterrupted()) {
            recorderThread.mRecordContinue = false;
            recorderThread.stopRecording();
            Toast.makeText(AddActivity.this, "Stop Recording.", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.ic_mic_blue_24dp);
            if(!imagePlay.isEnabled())
                imagePlay.setEnabled(true);
            recordState = false;
        }
    }

    private void startPlaying() {

        try {
            stopPlayingBtn();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (recorderThread != null) {
            playState = true;
            recorderThread.mPlayContinue = true;
            recorderThread.playAudio();
            Toast.makeText(AddActivity.this, "Start Playing...", Toast.LENGTH_SHORT).show();
            imagePlay.setImageResource(R.drawable.ic_pause_circle_filled_blue_40dp);
        }
    }

    static void stopPlayingBtn() {
        if (recorderThread != null && !recorderThread.playThread.isInterrupted()) {
            recorderThread.stopPlaying();
            imagePlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            playState = false;
        }
    }
}