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
import com.cczq.missionforce.Model.User;
import com.cczq.missionforce.R;

import java.util.ArrayList;

/**
 * Created by bb on 2016/12/7.
 */

public class UserListAdapter extends BaseAdapter {

    public ArrayList<User> usersData = new ArrayList<User>();

    private View.OnClickListener onClickListener;
    private LayoutInflater inflater = null;


    public UserListAdapter(Context context, View.OnClickListener onClickListener) {
        super();
        inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return usersData.size();
    }

    @Override
    public Object getItem(int position) {
        return usersData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_listitem_layout, parent, false);
        }
        //设置GroupName
        TextView textView = (TextView) convertView.findViewById(R.id.userName_TextView);
        textView.setText(usersData.get(position).userName);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .width(100)  // width in px
                .height(100) // height in px
                .endConfig()
                .buildRect(usersData.get(position).userName.substring(0,1), ColorGenerator.MATERIAL.getRandomColor());

        ImageView imageView = (ImageView)convertView.findViewById(R.id.user_ImageView);
        imageView.setImageDrawable(drawable);

        convertView.setOnClickListener(this.onClickListener);
        convertView.setTag(position);

        return convertView;
    }

    public ArrayList<User> getData() {
        return usersData;
    }
}
