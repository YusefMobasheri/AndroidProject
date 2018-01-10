package com.rahbod.androidproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.rahbod.androidproject.R.id.imagePlay;

public class SoundAdapter extends BaseAdapter {
    Context mContext;
    DbHelper mDb;
    List<Sound> sounds;

    public SoundAdapter(Context c ,List<Sound> sounds) {
        mContext = c;
        mDb = new DbHelper(mContext);
        this.sounds = sounds;
    }

    @Override
    public int getCount() {
        return sounds.size();
    }

    @Override
    public Sound getItem(int position) {
        return sounds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.sound_item, null);

        final TextView title = (TextView) v.findViewById(R.id.tvTitle);
        title.setText(getItem(position).getTitle());

        TextView user = (TextView) v.findViewById(R.id.tvUsername);
        user.setText(getItem(position).getUser());

        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekbar);

        final ImageView imageView = (ImageView) v.findViewById(R.id.btnPlay);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playState) {
                    stopPlayingBtn();
                    seekBar.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_play_circle_filled_white_40dp);
                } else {
                    recorderThread = new RecorderThread();
                    try {
                        recorderThread.setStream(getItem(position).getVoiceUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    seekBar.setMax((int) getItem(position).getVoiceUri().length / 2);
//                    seekBar.setVisibility(View.VISIBLE);
//                    title.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(R.drawable.ic_pause_circle_filled_white_40dp);
                    startPlaying();
                }
            }
        });
        
        final TextView btnDelete = (TextView) v.findViewById(R.id.tvDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playState) {
                    stopPlayingBtn();
                }
                mDb.deleteSound(getItem(position).getId());
                Log.e("dsfsdnf", mDb+"");
                sounds.remove(position);
                notifyDataSetChanged();
            }
        });
        return v;
    }

    private RecorderThread recorderThread = null;
    private boolean playState = false;

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
        }
    }

    private void stopPlayingBtn() {
        if (recorderThread != null && !recorderThread.playThread.isInterrupted()) {
            recorderThread.stopPlaying();
            playState = false;
        }
    }

    void refreshSounds(List<Sound> sounds) {
        this.sounds.clear();
        this.sounds.addAll(sounds);
    }
}