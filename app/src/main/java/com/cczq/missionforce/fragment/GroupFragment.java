package com.cczq.missionforce.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.AppController;
import com.cczq.missionforce.MainActivity;
import com.cczq.missionforce.Model.Group;
import com.cczq.missionforce.R;
import com.cczq.missionforce.adapter.GroupGridAdapter;
import com.cczq.missionforce.groupactivity.AddGroupActivity;
import com.cczq.missionforce.groupactivity.GroupMainActivity;
import com.cczq.missionforce.utils.configURL;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.GridViewWithHeaderAndFooter;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class GroupFragment extends Fragment implements View.OnClickListener{

    private static final String LOG_TAG = MissionFragment.class.getSimpleName();

//    private ArrayList<Group> groupData = new ArrayList<Group>();

    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private GridViewWithHeaderAndFooter mGridView;
    private GroupGridAdapter groupGridAdapter;

//    Handler handler = new Handler();

//    int page = 0;

    private Button createGroupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater .inflate(R.layout.fragment_group, container, false);
        createGroupBtn = (Button)view.findViewById(R.id.createGroupBtn);
        ptrClassicFrameLayout = (PtrClassicFrameLayout)view.findViewById(R.id.group_gridView_frame);
        mGridView = (GridViewWithHeaderAndFooter)view.findViewById(R.id.group_gridview);

        createGroupBtn.setOnClickListener(this);

        //初始化适配器  并利用提供的接口实现响应事件
        groupGridAdapter = new GroupGridAdapter(getActivity(),this);
        mGridView.setAdapter(groupGridAdapter);

        initData();
        initiateRefresh();
        return view;
    }

    //初始化设置刷新的Date
    private void initData() {

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                page = 0;
                groupGridAdapter.groupData.clear();

                initiateRefresh();
                ptrClassicFrameLayout.loadMoreComplete(false);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {

                groupGridAdapter.notifyDataSetChanged();
                ptrClassicFrameLayout.loadMoreComplete(false);
//                page++;
                Toast.makeText(getActivity(), "load more complete", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //创建小组的点击事件
    @Override
    public void onClick(View view) {
        //如果为创建小组
        if (view.getId() == R.id.createGroupBtn)
        {
            Intent intent = new Intent(getActivity(), AddGroupActivity.class);
            startActivity(intent);

        }
        else
        {
            Group group = groupGridAdapter.groupData.get((int)view.getTag());
            Intent intent = new Intent(getActivity(), GroupMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", group);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);
        }
    }

    //异步刷新
    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");
        //用来取消请求
//        setRefreshing(true);
        String tag_string_req = "req_check_mission";

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHECKGROUP, new Response.Listener<String>() {
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
//                    setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                MainActivity mainActivity = (MainActivity) getActivity();
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                //存储UID
                params.put("UID", Integer.toString(mainActivity.session.UID()));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //完成解析json
    private void onRefreshComplete(JSONArray json) {
        //数据
//        missionListAdapter.missionData.clear();
        for (int i = 0; i < json.length(); i++) {
            Group group = new Group();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                group.GID = jsonObject.getInt("GID");
                group.groupName = jsonObject.getString("group_name");
                group.groupLeader = jsonObject.getInt("group_leader");
                group.groupDescription = jsonObject.getString("group_description");
                groupGridAdapter.groupData.add(group);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        groupGridAdapter.notifyDataSetChanged();
        ptrClassicFrameLayout.refreshComplete();
        ptrClassicFrameLayout.setLoadMoreEnable(true);
    }


}

