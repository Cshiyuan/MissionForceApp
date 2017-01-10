package com.cczq.missionforce.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.MainActivity;
import com.cczq.missionforce.Model.Mission;
import com.cczq.missionforce.R;
import com.cczq.missionforce.countDownActivity;
import com.cczq.missionforce.adapter.MissionListAdapter;
import com.cczq.missionforce.utils.configURL;
import com.cczq.missionforce.widget.SwipeRefreshListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class MissionFragment extends SwipeRefreshListFragment {

    private static final String LOG_TAG = MissionFragment.class.getSimpleName();

    private MissionListAdapter missionListAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        missionListAdapter = new MissionListAdapter(getActivity());
        setListAdapter(missionListAdapter);
        setColorScheme(R.color.greenyellow, R.color.lawngreen, R.color.mediumspringgreen, R.color.lightgreen);

        //下拉刷新
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                initiateRefresh();
            }
        });
        initiateRefresh();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //System.out.println("Click On List Item!!!");
        Mission mission = missionListAdapter.missionData.get(position);
        Intent intent = new Intent(getActivity(), countDownActivity.class);
        //序列化传递对象
        Bundle bundle = new Bundle();
        bundle.putSerializable("mission", mission);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");
        //用来取消请求
        setRefreshing(true);
        String tag_string_req = "req_check_mission";

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
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
                    setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
//                    Toast.makeText(getApplicationContext(),
//                            error.getMessage(), Toast.LENGTH_LONG).show();
                setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                MainActivity mainActivity = (MainActivity) getActivity();
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", Integer.toString(mainActivity.session.UID()));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onRefreshComplete(JSONArray json) {
        //数据

        missionListAdapter.missionData.clear();
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
                missionListAdapter.missionData.add(mission);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // setListAdapter(missionListAdapter);
        missionListAdapter.notifyDataSetChanged();
        setRefreshing(false);
    }

}