package com.cczq.missionforce.groupactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.BaseActivity;
import com.cczq.missionforce.Model.Mission;
import com.cczq.missionforce.R;
import com.cczq.missionforce.adapter.MissionListAdapter;
import com.cczq.missionforce.utils.configURL;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bb on 2016/12/7.
 */

public class CheckMemberMissionActivity extends BaseActivity {

    PtrClassicFrameLayout ptrClassicFrameLayout;
    ListView mListView;
    private List<String> mData = new ArrayList<String>();
    private MissionListAdapter mAdapter;
    Handler handler = new Handler();

    int UID;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmembermission);

        Intent intent = getIntent();
        UID = intent.getIntExtra("UID",8);

        mAdapter = new MissionListAdapter(this);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_list_view_frame);
        mListView = (ListView) this.findViewById(R.id.test_list_view);
        initData();
        initiateRefresh();
    }

    private void initData() {
//        mAdapter = new ListViewAdapter(this, mData);
        mListView.setAdapter(mAdapter);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                page = 0;
                mData.clear();
                initiateRefresh();
                if(mData.size() == 0)
                    mData.add("无");
                ptrClassicFrameLayout.loadMoreComplete(false);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {

                mAdapter.notifyDataSetChanged();
                ptrClassicFrameLayout.loadMoreComplete(false);
//                page++;
                Toast.makeText(CheckMemberMissionActivity.this, "load more complete", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initiateRefresh() {
//        Log.i(LOG_TAG, "initiateRefresh");
        //用来取消请求
//        setRefreshing(true);
        String tag_string_req = "req_check_mission_member";

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHEACKMISSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");

                    //检查error节点
                    if (ret == 200) {
                        onRefreshComplete(data.getJSONArray("info"));
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        ptrClassicFrameLayout.refreshComplete();
                        ptrClassicFrameLayout.setLoadMoreEnable(true);
                        Toast.makeText(CheckMemberMissionActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
//                    setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
//                    Toast.makeText(getApplicationContext(),
//                            error.getMessage(), Toast.LENGTH_LONG).show();
//                setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
//                MainActivity mainActivity = (MainActivity) getActivity();
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", Integer.toString(UID));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onRefreshComplete(JSONArray json) {
        //数据

//        if()
        mAdapter.missionData.clear();
        for (int i = 0; i < json.length(); i++) {
            Mission mission = new Mission();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                mission.missionNameText = jsonObject.getString("mission_name");
                mission.missionDescriptionText = jsonObject.getString("mission_description");
                mission.groupNameText = jsonObject.getString("group_name");
                mission.time = jsonObject.getInt("mission_time");
                mission.timeText = Integer.toString(mission.time) + "分钟";
                mission.MID = jsonObject.getInt("MID");
                mAdapter.missionData.add(mission);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // setListAdapter(missionListAdapter);
        mAdapter.notifyDataSetChanged();
//        setRefreshing(false);
        ptrClassicFrameLayout.refreshComplete();
        ptrClassicFrameLayout.setLoadMoreEnable(true);
    }


}
