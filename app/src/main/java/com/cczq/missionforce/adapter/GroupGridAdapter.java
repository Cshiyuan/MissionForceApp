package com.cczq.missionforce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.cczq.missionforce.Model.Group;
import com.cczq.missionforce.R;

import java.util.ArrayList;

/**
 * Created by bb on 2016/12/5.
 */

public class GroupGridAdapter extends BaseAdapter {


    public ArrayList<Group> groupData = new ArrayList<Group>();

    private View.OnClickListener onClickListener;
    private LayoutInflater inflater = null;

    public GroupGridAdapter(Context context,View.OnClickListener onClickListener) {
        super();
        inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return groupData.size();
    }

    @Override
    public Object getItem(int position) {
        return groupData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_griditem_layout, parent, false);
        }
        //设置GroupName
        TextView textView = (TextView) convertView.findViewById(R.id.groupName_TextView);
        textView.setText(groupData.get(position).groupName);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .width(100)  // width in px
                .height(100) // height in px
                .endConfig()
                .buildRect(groupData.get(position).groupName.substring(0,1), ColorGenerator.MATERIAL.getRandomColor());

        ImageView imageView = (ImageView)convertView.findViewById(R.id.group_ImageView);
        imageView.setImageDrawable(drawable);

        convertView.setOnClickListener(this.onClickListener);
        convertView.setTag(position);

        return convertView;
    }

    public ArrayList<Group> getData() {
        return groupData;
    }
}
