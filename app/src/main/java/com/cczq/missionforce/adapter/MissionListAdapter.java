package com.cczq.missionforce.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.cczq.missionforce.Model.Mission;
import com.cczq.missionforce.R;

import java.util.ArrayList;

/**
 * Created by Shyuan on 2016/10/11.
 */


public class MissionListAdapter extends BaseAdapter {

    private class MissionViewHolder {
        //  public ImageView animal;
        public TextView missionNameTextView;
        public TextView groupNameTextView;
        public TextView timeTextView;
        public ImageView missionImageView;
        // public ImageView speaker;
    }


    public ArrayList<Mission> missionData = new ArrayList<Mission>();

    private LayoutInflater mInflater = null;

    public MissionListAdapter(Context context) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return missionData.size();
    }

    @Override
    public Object getItem(int position) {
        return missionData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MissionViewHolder holder = null;
        if (convertView == null) {
            holder = new MissionViewHolder();
            convertView = mInflater.inflate(R.layout.mission, null);
            holder.missionImageView = (ImageView) convertView.findViewById(R.id.missionImageView);
            holder.missionNameTextView = (TextView) convertView.findViewById(R.id.cn_word);
            holder.groupNameTextView = (TextView) convertView.findViewById(R.id.en_word);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (MissionViewHolder) convertView.getTag();
        }


        final Drawable drawable = getRectWithAnimation(missionData.get(position).missionNameText, position, ColorGenerator.MATERIAL.getRandomColor());

        holder.missionImageView.setImageDrawable(drawable);
        //修复动画在一些低于4.4的手机设备
        holder.missionImageView.post(new Runnable() {
            @Override
            public void run() {
                ((AnimationDrawable) drawable).stop();
                ((AnimationDrawable) drawable).start();
            }
        });
        holder.missionNameTextView.setText(missionData.get(position).missionNameText);
        holder.groupNameTextView.setText(missionData.get(position).groupNameText);
        holder.timeTextView.setText(missionData.get(position).timeText);
        return convertView;
    }

    private Drawable getRectWithAnimation(String string, int delay, int color) {
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .rect();

        AnimationDrawable animationDrawable = new AnimationDrawable();

        for (int i = 0; i < string.length() - 1; i++) {
            Log.d("debug", string.substring(i, i + 1));
            TextDrawable frame = builder.build(string.substring(i, i + 1), color);
            animationDrawable.addFrame(frame, 900 + delay * 100);
        }

        animationDrawable.setOneShot(false);
        animationDrawable.start();

        return animationDrawable;
    }
}

