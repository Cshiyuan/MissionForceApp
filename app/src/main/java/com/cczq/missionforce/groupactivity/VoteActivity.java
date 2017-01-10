package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.BaseActivity;
import com.cczq.missionforce.R;
import com.cczq.missionforce.loginresgister.utils.SessionManager;
import com.cczq.missionforce.utils.configURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bb on 2016/12/14.
 */

public class VoteActivity extends BaseActivity {

    int TID;

    private List<String> voteOptions = new ArrayList<String>();
    private List<String> voteOID = new ArrayList<String>();
    private List<TextView> TextViewOptions = new ArrayList<TextView>();



    Button okbutton;
    TextView votetitle;
    TextView opt1;
    TextView opt2;
    TextView opt3;
    TextView opt4;
    TextView opt5;
    RadioButton button1;
    RadioButton button2;
    RadioButton button3;
    RadioButton button4;
    RadioButton button5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);


        Intent intent = getIntent();
//        TID = (Group) intent.getSerializableExtra("TID");
        TID = intent.getIntExtra("TID", -1);
        String Theme = intent.getStringExtra("ThemeName");


        okbutton=(Button)findViewById(R.id.button);

        votetitle=(TextView)findViewById(R.id.textViewVoteTitle);
        votetitle.setText(Theme);

        opt1=(TextView)findViewById(R.id.textViewopt1);
        opt2=(TextView)findViewById(R.id.textViewopt2);
        opt3=(TextView)findViewById(R.id.textViewopt3);
        opt4=(TextView)findViewById(R.id.textViewopt4);
        opt5=(TextView)findViewById(R.id.textViewopt5);

        TextViewOptions.add(opt1);
        TextViewOptions.add(opt2);
        TextViewOptions.add(opt3);
        TextViewOptions.add(opt4);
        TextViewOptions.add(opt5);

        button1 = (RadioButton) findViewById(R.id.radioButton1);
        button2 = (RadioButton) findViewById(R.id.radioButton2);
        button3 = (RadioButton) findViewById(R.id.radioButton3);
        button4 = (RadioButton) findViewById(R.id.radioButton4);
        button5 = (RadioButton) findViewById(R.id.radioButton5);

        okbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                upVoteInformation();
            }
        });

        if(TID != -1)
        {
            getVoteInformation();
        }

//        getVoteInformation();
    }

    public void upVoteInformation(){
        int choiceNum = getChoice();
        if(choiceNum == -1 || choiceNum > voteOID.size())
        {
            Toast.makeText(getApplicationContext(),"请选择正确的选项",Toast.LENGTH_SHORT);
        }
        else
        {
            toVote(voteOID.get(choiceNum-1));
        }
    }


    private void toVote(final String OID)
    {
        //用来取消请求
        String tag_string_req = "req_to_vote";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_TOVOTE, new Response.Listener<String>() {

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
//                        onRefreshComplete(data.getJSONArray("info"));
                        if(data.getString("info").equals("1"))
                        {
                            Toast.makeText(getApplicationContext(),
                                    "投票成功", Toast.LENGTH_LONG).show();
                            getVoteInformation();
                        }
                        else {
                            String errorMsg = jObj.getString("msg") + data.getString("info");
                            //显示错误信息
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

//                        mAdapter.notifyDataSetChanged();
//                        ptrClassicFrameLayout.refreshComplete();
//                        ptrClassicFrameLayout.setLoadMoreEnable(true);
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
                params.put("OID", String.valueOf(OID));
                SessionManager session =  new SessionManager(getApplicationContext());
                params.put("UID", Integer.toString(session.UID()));
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void getVoteInformation()
    {

        //用来取消请求
        String tag_string_req = "req_check_vote";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHECKVOTEINFO, new Response.Listener<String>() {

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
                        onRefreshComplete(data.getJSONArray("info"));

                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

//                        mAdapter.notifyDataSetChanged();
//                        ptrClassicFrameLayout.refreshComplete();
//                        ptrClassicFrameLayout.setLoadMoreEnable(true);
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
                params.put("TID", String.valueOf(TID));
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onRefreshComplete(JSONArray json)
    {
        voteOID.clear();
        voteOptions.clear();
        for (int i = 0; i < json.length(); i++) {
//            User user = new User();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
//                {
                voteOID.add(jsonObject.getString("OID"));
                voteOptions.add(jsonObject.getString("OptionName") + "    " + jsonObject.getString("VoteNumber"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setVoteOptions();
    }

    //根据voteOptions设置投票选项
    private void setVoteOptions()
    {
        if(!voteOptions.isEmpty())
        {
            for(int i = 0; i < voteOptions.size(); i++)
            {
                TextViewOptions.get(i).setText(voteOptions.get(i));
            }
        }
    }

    private int getChoice()
    {
        if(button1.isChecked())
        {
            return 1;
        }
        if(button2.isChecked())
        {
            return 2;
        }
        if(button3.isChecked())
        {
            return 3;
        }
        if(button4.isChecked())
        {
            return 4;
        }
        if(button5.isChecked())
        {
            return 5;
        }
        return -1;
    }
}

