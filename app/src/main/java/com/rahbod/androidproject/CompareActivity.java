package com.rahbod.androidproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.y;

public class CompareActivity extends AppCompatActivity {
    private static RecorderThread recorderThread = null;
    ImageButton btnCompareVoice;
    TextView tvShowTitle;
    SQLiteDatabase db;

    boolean recordState = false;
    static boolean playState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        db = (new DbHelper(this)).getReadableDatabase();

        tvShowTitle = (TextView) findViewById(R.id.tvShowTitle);

        btnCompareVoice = (ImageButton) findViewById(R.id.btnCompareVoice);
        btnCompareVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(CompareActivity.this, "Down", Toast.LENGTH_SHORT).show();
                    final Animation myAnim = AnimationUtils.loadAnimation(CompareActivity.this, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    btnCompareVoice.startAnimation(myAnim);
                    btnCompareVoice.setImageResource(R.drawable.ic_mic_red_24dp);
                    startRecording();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(CompareActivity.this, "Up", Toast.LENGTH_SHORT).show();
                    stopRecordingBtn();
                    compareVoice(recorderThread.getStream());
                    btnCompareVoice.setImageResource(R.drawable.ic_mic_blue_24dp);
                    return true;
                }
                return false;
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
        Toast.makeText(CompareActivity.this, "Start Recording...", Toast.LENGTH_SHORT).show();
//        btnCompareVoice.setImageResource(R.drawable.ic_mic_red_24dp);

    }

    private void stopRecordingBtn() {
        if (recorderThread != null && !recorderThread.recordThread.isInterrupted()) {
            recorderThread.mRecordContinue = false;
            recorderThread.stopRecording();
            Toast.makeText(CompareActivity.this, "Stop Recording.", Toast.LENGTH_SHORT).show();
//            imageButton.setImageResource(R.drawable.ic_mic_blue_24dp);
//            if(!imagePlay.isEnabled())
//                imagePlay.setEnabled(true);
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
            Toast.makeText(CompareActivity.this, "Start Playing...", Toast.LENGTH_SHORT).show();
//            imagePlay.setImageResource(R.drawable.ic_pause_circle_filled_blue_40dp);
        }
    }

    static void stopPlayingBtn() {
        if (recorderThread != null && !recorderThread.playThread.isInterrupted()) {
            recorderThread.stopPlaying();
//            imagePlay.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            playState = false;
        }
    }

    private static final int CHUNK_SIZE = 1000;
    private static final int LOWER_LIMIT = 30;
    private static final int UPPER_LIMIT = 300;

    private String compareVoice(byte[] stream) {
        Complex[][] streamFFTChunks;
        if (stream.length > 0) {
            streamFFTChunks = calculateFFT(stream);
            for (int i = 0; i < streamFFTChunks.length; i++) {
                for (int line = 1; line < CHUNK_SIZE; line++) {
                    for (int freq = LOWER_LIMIT; freq < UPPER_LIMIT - 1; freq++) {
                        //Get the magnitude:
                        double mag = Math.log(streamFFTChunks[i][freq].abs() + 1);
                        //Find out which range we are in:
                        int index = getIndex(freq);

                        //Save the highest magnitude and corresponding frequency:
//                        if (mag > highscores[index]) {
//                            highscores[index] = mag;
//                            recordPoints[index] = freq;
//                        }
                    }
                }
            }
        }
        return "";
    }


    List<Sound> getSounds() {
        Cursor c = db.rawQuery("SELECT * FROM sounds", null);
        if (c.getCount() > 0) {
            List<Sound> data = new ArrayList<>();
            while (c.moveToNext()) {
                Sound sound = new Sound(c.getInt(c.getColumnIndex("id")), c.getBlob(c.getColumnIndex("voice")), c.getString(c.getColumnIndex("title")), c.getString(c.getColumnIndex("username")));
                data.add(sound);
            }
            c.close();
            return data;
        }
        return null;
    }

    public static final int[] RANGE = new int[]{40, 80, 120, 180, UPPER_LIMIT + 1};

    //Find out in which range
    public static int getIndex(int freq) {
        int i = 0;
        while (RANGE[i] < freq) i++;
        return i;
    }

    public Complex[][] calculateFFT(byte[] signal) {
        int amountPossible = signal.length / CHUNK_SIZE;
        Complex[][] results = new Complex[amountPossible][];
        for (int times = 0; times < amountPossible; times++) {
            Complex[] complex = new Complex[CHUNK_SIZE]; // complex per chunk
            double temp;
            for (int i = 0; i < CHUNK_SIZE; i++) {
                //Put the time domain data into a complex number with imaginary part as 0:
                temp = (double) ((signal[(times * CHUNK_SIZE) + 2*i] & 0xFF) | (signal[(times * CHUNK_SIZE) + (2*i) + 1] << 8)) / 32768.0F;
                complex[i] = new Complex(temp, 0);
            }
            //Perform FFT analysis on the chunk:
            results[times] = FFT.fft(complex);
        }
        return results;
//        final int mNumberOfFFTPoints = 1024;
//        double mMaxFFTSample;
//
//        double temp;
//        Complex[] y;
//        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
//        double[] absSignal = new double[mNumberOfFFTPoints / 2];
//
//        for (int i = 0; i < mNumberOfFFTPoints; i++) {
//            temp = (double) ((signal[2 * i] & 0xFF) | (signal[2 * i + 1] << 8)) / 32768.0F;
//            complexSignal[i] = new Complex(temp, 0.0);
//        }
//
////        y = FFT.fft(complexSignal); // --> Here I use FFT class
////        mMaxFFTSample = 0.0;
////        for (int i = 0; i < (mNumberOfFFTPoints / 2); i++) {
////            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
////            if (absSignal[i] > mMaxFFTSample) {
////                mMaxFFTSample = absSignal[i];
////            }
////        }
////        return absSignal;
    }
}