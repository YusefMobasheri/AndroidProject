package com.rahbod.androidproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class SoundAdapter extends BaseAdapter {
    Context mContext;
    List<Sound> sounds;

    public SoundAdapter(Context c, List<Sound> sounds) {
        mContext = c;
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
        return sounds.get(position).getId();
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
                Uri voiceUri = Uri.parse(getItem(position).getVoiceUri());
                seekBar.setVisibility(View.VISIBLE);
                title.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.ic_pause_circle_filled_white_40dp);
            }
        });
        return v;
    }
}