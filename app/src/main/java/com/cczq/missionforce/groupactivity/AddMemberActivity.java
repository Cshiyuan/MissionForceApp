package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bb on 2016/12/6.
 */

public class AddMemberActivity extends BaseActivity {

    private EditText memberEmail;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmember);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");

        Button add = (Button)findViewById(R.id.button);
        memberEmail = (EditText)findViewById(R.id.editText);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(memberEmail.getText().toString().matches("\\w+@\\w+\\.\\w+"))
                {
//                    Toast.makeText(AddMemberActivity.this, "添加成功：" + memberEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                    createVote(group.GID,memberEmail.getText().toString());
                    memberEmail.getText().clear();
                }
                else
                {
                    Toast.makeText(AddMemberActivity.this, "邮箱非法！", Toast.LENGTH_SHORT).show();
                    memberEmail.getText().clear();
                }
            }
        });
    }



    //向小组添加成员
    private void createVote(final int GID, final String email)
    {

        //用来取消请求
        String tag_string_req = "req_add_member_group";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_ADDMEMBERTOGROUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");
                    String msg = data.getString("msg");

                    //检查error节点
                    if (ret == 200 && code == 200 && msg.equals("添加成功")) {

                        Toast.makeText(getApplicationContext(),
                                "添加成功", Toast.LENGTH_LONG).show();
                        finish();
//                        cleanAllEditText();

                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg + "不存在用户", Toast.LENGTH_LONG).show();
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
                params.put("email", email);
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
