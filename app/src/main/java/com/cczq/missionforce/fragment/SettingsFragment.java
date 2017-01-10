package com.cczq.missionforce.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cczq.missionforce.MainActivity;
import com.cczq.missionforce.R;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ToggleButton shake;
    private ToggleButton ring;
    private TextView helpandfeedback;
    private TextView about;
    private Spinner spinner;
    private Button logout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        shake = (ToggleButton)view.findViewById(R.id.shake); // 获取到控件
        shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //选中
                    Toast.makeText(getActivity(),"Shake ON",Toast.LENGTH_LONG).show();
                }else{
                    //未选中
                    Toast.makeText(getActivity(),"Shake OFF",Toast.LENGTH_LONG).show();
                }
            }
        });// 添加监听事件

        ring = (ToggleButton)view.findViewById(R.id.ring); // 获取到控件
        ring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //选中
                    Toast.makeText(getActivity(),"Ring ON",Toast.LENGTH_LONG).show();
                }else{
                    //未选中
                    Toast.makeText(getActivity(),"Ring OFF",Toast.LENGTH_LONG).show();
                }
            }
        });// 添加监听事件

        helpandfeedback=(TextView)view.findViewById(R.id.helpandfeedback);
        helpandfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至帮助与反馈界面
                Toast.makeText(getActivity(),"Jump to Help&Feedback",Toast.LENGTH_LONG).show();
            }
        });

        about=(TextView)view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至关于界面
                Toast.makeText(getActivity(),"Jump to About",Toast.LENGTH_LONG).show();
            }
        });

        spinner=(Spinner)view.findViewById(R.id.spinner);
        spinner.getSelectedItem();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String time=parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(),time,Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        logout=(Button)view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout) {
            Log.d("Try to logout", "debug");
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.logoutUser();
        }
    }
}
