package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.BaseActivity;
import com.cczq.missionforce.Model.Group;
import com.cczq.missionforce.R;
import com.cczq.missionforce.utils.configURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bb on 2016/12/7.
 */

public class CreateVoteActivity extends BaseActivity {

    private Group group;

    private Button pstbutton;
    private EditText title;
    private EditText option1;
    private EditText option2;
    private EditText option3;
    private EditText option4;
    private EditText option5;
    private RadioButton btn1;
    private RadioButton btn2;
    private RadioButton btn3;
    private RadioButton btn4;
    private RadioButton btn5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createvote);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");

        pstbutton =(Button)findViewById(R.id.postbutton);
        title=(EditText)findViewById(R.id.editTexttitle);
        option1=(EditText)findViewById(R.id.editText1);
        option2=(EditText)findViewById(R.id.editText2);
        option3=(EditText)findViewById(R.id.editText3);
        option4=(EditText)findViewById(R.id.editText4);
        option5=(EditText)findViewById(R.id.editText5);

        btn1 = (RadioButton)findViewById(R.id.radioButton1);
        btn2 = (RadioButton)findViewById(R.id.radioButton2);
        btn3 = (RadioButton)findViewById(R.id.radioButton3);
        btn4 = (RadioButton)findViewById(R.id.radioButton4);
        btn5 = (RadioButton)findViewById(R.id.radioButton5);

        pstbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tle = title.getText().toString();
                String opt1=option1.getText().toString();
                String opt2=option2.getText().toString();
                String opt3=option3.getText().toString();
                String opt4=option4.getText().toString();
                String opt5=option5.getText().toString();
                voteimformation(tle,opt1,opt2,opt3,opt4,opt5);

            }
        });
    }

    public void voteimformation(String tle, String opt1, String opt2, String opt3, String opt4, String opt5){
//            btn1.isActivated()
//        String options = "";
        ArrayList<String> optionsList = new ArrayList<String>();
        if(btn1.isChecked())
            optionsList.add(opt1);
        if(btn2.isChecked())
            optionsList.add(opt2);
        if(btn3.isChecked())
            optionsList.add(opt3);
        if(btn4.isChecked())
            optionsList.add(opt4);
        if(btn5.isChecked())
            optionsList.add(opt5);
        String optionsString = "";
        for(int i = 0; i < optionsList.size(); i++) {
            if (i == optionsList.size() - 1) {
                optionsString = optionsString + optionsList.get(i);
            } else {
                optionsString = optionsString + optionsList.get(i) + ",";
            }

        }
        createVote(group.GID,tle,optionsString);
    }


    //发布任务到服务器
    private void createVote(final int GID, final String ThemeName,final String options)
    {

        //用来取消请求
        String tag_string_req = "req_create_vote";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CREATEVOTE, new Response.Listener<String>() {

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
                    if (ret == 200 && code == 200 && msg.equals("发起成功")) {
//                        finish();
//                        cleanAllEditText();
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
                params.put("GID", Integer.toString(GID));
                params.put("ThemeName", ThemeName);
                params.put("Options", options);
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //判断有多少被激活了
    private int isActivated()
    {
        int i = 0;
        if(btn1.isChecked())
            i++;
        if(btn2.isChecked())
            i++;
        if(btn3.isChecked())
            i++;
        if(btn4.isChecked())
            i++;
        if(btn5.isChecked())
            i++;
        return i;
    }
}
