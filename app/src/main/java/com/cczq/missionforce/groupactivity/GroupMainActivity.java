package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import com.cczq.missionforce.adapter.UserListAdapter;
import com.cczq.missionforce.utils.configURL;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bb on 2016/12/6.
 */

public class GroupMainActivity extends BaseActivity implements View.OnClickListener{


    PtrClassicFrameLayout ptrClassicFrameLayout;
    ListView mListView;
//    private List<String> mData = new ArrayList<String>();
    private UserListAdapter mAdapter;
    Handler handler = new Handler();

    int page = 0;


    private Group group;  //组模型

    private TextView groupName;
    private Button addGroupMemberButton;
    private Button assignMissionButton;
    private Button createVoteButton;
    private Button checkVoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maingroup);
        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");

        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.list_view_frame);
        mListView = (ListView) this.findViewById(R.id.list_view);

        mAdapter = new UserListAdapter(this, this);
        mListView.setAdapter(mAdapter);

        initData();

        groupName = (TextView)findViewById(R.id.main_groupName_TextView);
        groupName.setText(group.groupName);

        addGroupMemberButton = (Button)findViewById(R.id.addGroupMember_Button);
        assignMissionButton = (Button)findViewById(R.id.assignMission_Button);
        createVoteButton = (Button)findViewById(R.id.createVote_Button);
        checkVoteButton = (Button)findViewById(R.id.checkVoteButton);
        addGroupMemberButton.setOnClickListener(this);
        assignMissionButton.setOnClickListener(this);
        createVoteButton.setOnClickListener(this);
        checkVoteButton.setOnClickListener(this);




//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getGroupUsers();
    }



    //下面的业务按钮跳转到不同的界面
    @Override
    public void onClick(View view) {
        Intent intent;
        Bundle bundle;
        switch (view.getId()){
            case R.id.addGroupMember_Button:
                intent = new Intent(this, AddMemberActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("group", group);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.assignMission_Button:
                intent = new Intent(this, AssignMissionActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("group", group);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.createVote_Button:
                intent = new Intent(this, CreateVoteActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("group", group);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.checkVoteButton:
                intent = new Intent(this, CheckVoteActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("group", group);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }

        if(view.getId() == -1) {

            int UID = mAdapter.usersData.get((int) view.getTag()).UID;
//            Log.d("asdad", String.valueOf(view.getId()));
            intent = new Intent(this, CheckMemberMissionActivity.class);
            bundle = new Bundle();
//        bundle.putSerializable("group", group);
            bundle.putInt("UID", UID);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }


    private void initData() {

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 0;
                mAdapter.usersData.clear();
                getGroupUsers();
                ptrClassicFrameLayout.loadMoreComplete(false);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {

                mAdapter.notifyDataSetChanged();
                ptrClassicFrameLayout.loadMoreComplete(false);
                page++;
                Toast.makeText(GroupMainActivity.this, "load more complete", Toast.LENGTH_SHORT).show();

            }
        });
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
                        onRefreshComplete(data.getJSONArray("info"));
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

    //完成解析json
    private void onRefreshComplete(JSONArray json) {
        for (int i = 0; i < json.length(); i++) {
            User user = new User();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
//                group.GID = jsonObject.getInt("GID");
                user.userName = jsonObject.getString("username");
                user.UID = jsonObject.getInt("UID");
                user.email = jsonObject.getString("email");
                user.introduction = jsonObject.getString("introduction");
                mAdapter.usersData.add(user);
//                mAdapter.usersData.add(user.userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
        ptrClassicFrameLayout.refreshComplete();
        ptrClassicFrameLayout.setLoadMoreEnable(true);
    }
}
