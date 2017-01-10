package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.BaseActivity;
import com.cczq.missionforce.Model.Group;
import com.cczq.missionforce.Model.User;
import com.cczq.missionforce.R;
import com.cczq.missionforce.utils.configURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bb on 2016/12/6.
 */

public class AssignMissionActivity extends BaseActivity {


    private ArrayList<User> usersData = new ArrayList<User>(); //用户数据模型
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNameArray = new ArrayList<String>();


    private Group group;
    private EditText missionTitleEditText;
    private EditText missionDetailEditText;
    private EditText missionTimeEditText;
    private Button assignMissionButton;
    private Spinner spinnerUserPicker;
    private DatePicker missionDeadLineDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmisson);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");

        assignMissionButton = (Button)findViewById(R.id.AssignBtn);
        missionTitleEditText = (EditText)findViewById(R.id.Mtitle) ;
        missionDetailEditText = (EditText)findViewById(R.id.Mdetail) ;
        missionTimeEditText = (EditText)findViewById(R.id.Mtime);
        spinnerUserPicker = (Spinner)findViewById(R.id.spinner);
        missionDeadLineDatePicker = (DatePicker)findViewById(R.id.datePicker);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, userNameArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinnerUserPicker.setAdapter(adapter);
        spinnerUserPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

//                String[] languages = getResources().getStringArray(R.array.languages);
                Toast.makeText(AssignMissionActivity.this, usersData.get(pos).userName,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        assignMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputIsEmpty())
                    Toast.makeText(AssignMissionActivity.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(AssignMissionActivity.this,"发布成功！",Toast.LENGTH_LONG).show();
                    int position = spinnerUserPicker.getSelectedItemPosition();
                    int UID = usersData.get(position).UID;
                    int GID = group.GID;
                    String missionName = missionTitleEditText.getText().toString();
                    String missionDes = missionDetailEditText.getText().toString();
                    int missionTime =  Integer.parseInt(missionTimeEditText.getText().toString());
                    String missionDeadLineYear = Integer.toString(missionDeadLineDatePicker.getYear());
                    String missionDeadLineMonth = Integer.toString(missionDeadLineDatePicker.getMonth());
                    String missionDeadLineDay = Integer.toString(missionDeadLineDatePicker.getDayOfMonth());
                    String missionDeadLineString;
                    if(missionDeadLineDay.length() > 1)
                        missionDeadLineString = missionDeadLineYear+missionDeadLineMonth+missionDeadLineDay;
                    else
                        missionDeadLineString = missionDeadLineYear+missionDeadLineMonth+"0"+missionDeadLineDay;

                    assignMission(UID,GID,missionTime,missionName,missionDes,missionDeadLineString);
                }
            }
        });


        getGroupUsers();
    }


    //判断用户是否将所需数据填写完成
    private boolean inputIsEmpty()
    {
        if(missionTitleEditText.getText().toString().equals("") || missionDetailEditText.getText().toString().equals("") || missionTimeEditText.getText().toString().equals(""))
        {
            return true;
        }
        else
            return false;
    }

    //发布任务到服务器
    private void assignMission(final int UID, final int GID, final int missionTime, final String missionName, final String missionDescription, final String missionDeadline)
    {

        //用来取消请求
        String tag_string_req = "req_commit_group_users";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_COMMITGROUPUSERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Login Response: " + response.toString());
                // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");
                    String msg = data.getString("msg");

                    //检查error节点
                    if (ret == 200 && code == 200 && msg.equals("添加成功")) {
//                        finish();
                        cleanAllEditText();
                        Toast.makeText(getApplicationContext(),
                                "添加成功", Toast.LENGTH_LONG).show();
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //   Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //消失进度窗口
                //    hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数到LoginURl服务器
                Map<String, String> params = new HashMap<String, String>();
                //params.put("tag", "login");
                params.put("UID", Integer.toString(UID));
                params.put("GID", Integer.toString(GID));
                params.put("mission_name", missionName);
                params.put("mission_time", Integer.toString(missionTime));
                params.put("mission_deadline", missionDeadline);
                params.put("mission_description", missionDescription);
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //获取小组成员
    private void getGroupUsers() {

        //用来取消请求
        String tag_string_req = "req_get_group_users";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHECKGROUPUSERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Login Response: " + response.toString());
                // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");
//                    String msg = data.getString("msg");

                    //检查error节点
                    if (ret == 200 && code == 200) {
//                        finish();
                        putInToSpinner(data.getJSONArray("info"));
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //   Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数到LoginURl服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("GID", Integer.toString(group.GID));
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void putInToSpinner(JSONArray json)
    {
        this.userNameArray.clear();
        for (int i = 0; i < json.length(); i++) {
            User user = new User();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
//                group.GID = jsonObject.getInt("GID");
                user.userName = jsonObject.getString("username");
                user.UID = jsonObject.getInt("UID");
                user.email = jsonObject.getString("email");
                user.introduction = jsonObject.getString("introduction");
                this.usersData.add(user);
                this.userNameArray.add(user.userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void cleanAllEditText()
    {
        missionTitleEditText.getText().clear();
        missionDetailEditText.getText().clear();
        missionTimeEditText.getText().clear();
    }

}
