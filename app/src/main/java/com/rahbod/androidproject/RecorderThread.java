package com.rahbod.androidproject;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class RecorderThread {
    Context mContext;
    private static final String LOG_TAG = "Ee";
    final int SAMPLE_RATE = 44100; // The sampling rate
    boolean mRecordContinue; // Indicates if recording / playback should stop
    boolean mPlayContinue; // Indicates if recording / playback should stop
    ByteArrayOutputStream sampleBytes = null;
    AudioRecord record;
    AudioTrack audioTrack;

    public boolean hasPlaying = false;

    Thread recordThread;
    Runnable recordRun = new Runnable() {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

            // buffer size in bytes
            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                bufferSize = SAMPLE_RATE * 2;
            }

            byte[] audioBuffer = new byte[bufferSize*2];
            sampleBytes = new ByteArrayOutputStream();
            record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(LOG_TAG, "Audio Record can't initialize!");
                return;
            }
            record.startRecording();

            Log.v(LOG_TAG, "Start recording");

            int shortsRead = 0;

            while (mRecordContinue) {
                int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                shortsRead += numberOfShort;
                try {
                    sampleBytes.write(audioBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
        }
    };

    Thread playThread;
    Runnable playRun = new Runnable() {
        @Override
        public void run() {
            int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                bufferSize = SAMPLE_RATE * 2;
            }

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            Log.v(LOG_TAG, "Audio streaming started");
            hasPlaying = true;

            audioTrack.write(sampleBytes.toByteArray(), 0, sampleBytes.size());
            audioTrack.setNotificationMarkerPosition((int) bufferSize/2);
            audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    track.release();
                    mPlayContinue = false;
                }

                @Override
                public void onPeriodicNotification(AudioTrack track) {

                }
            });
            if (!mPlayContinue) {
                audioTrack.release();
            }
            Log.v(LOG_TAG, "Audio streaming finished.");
        }
    };

    void recordAudio() {
        Vibrator vibrate = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(100);
        recordThread = new Thread(recordRun);
        recordThread.start();
    }

    void stopRecording() {
        record.stop();
        record.release();
        recordThread.interrupt();
    }

    void playAudio() {
        playThread = new Thread(playRun);
        playThread.start();
    }

    void stopPlaying(){
        audioTrack.stop();
        audioTrack.release();
        playThread.interrupt();
    }

    void setStream(byte[] stream) throws IOException {
        sampleBytes = new ByteArrayOutputStream();
        sampleBytes.write(stream);
    }

    boolean hasStream() {
        try {
            return sampleBytes.size() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    byte[] getStream(){
        if(hasStream())
            return sampleBytes.toByteArray();
        return null;
    }
}