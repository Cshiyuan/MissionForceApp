package com.cczq.missionforce.groupactivity;

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
import com.cczq.missionforce.R;
import com.cczq.missionforce.loginresgister.utils.SessionManager;
import com.cczq.missionforce.utils.configURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bb on 2016/12/2.
 */

public class AddGroupActivity extends BaseActivity {

    EditText Group_name;//小组名称
    EditText Group_info;//小组描述
    Button create;//创建按钮
    Button cancel;//取消按钮
    String name;
    String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);


        create=(Button)findViewById(R.id.create);
        cancel=(Button)findViewById(R.id.cancel);
        Group_name=(EditText)findViewById(R.id.Group_name);
        Group_info=(EditText)findViewById(R.id.Group_info);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Group_name.getText().toString().length()>=1)
                {
                    name = Group_name.getText().toString();
                    info = Group_info.getText().toString();
                    Toast.makeText(AddGroupActivity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                    createGroup(name,info);
                    finish();
                }
                else Toast.makeText(AddGroupActivity.this, "信息非法！创建失败", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddGroupActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                Group_name.setText("");
                Group_info.setText("");
            }
        });

    }



    private void createGroup(final String groupName,final String groupDes)
    {
//        URL_CREATEGROUP
        //用来取消请求
        String tag_string_req = "req_create_group";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CREATEGROUP, new Response.Listener<String>() {

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
//                        putInToSpinner(data.getJSONArray("info"));
                        String errorMsg = jObj.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
//                params.put("GID", Integer.toString(group.GID));
                SessionManager session =  new SessionManager(getApplicationContext());
                params.put("UID", String.valueOf(session.UID()));
                params.put("groupName",groupName);
                params.put("groupDescription",groupDes);
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
