package com.cczq.missionforce.groupactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.cczq.missionforce.R;
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

public class CheckVoteActivity extends BaseActivity {

    PtrClassicFrameLayout ptrClassicFrameLayout;
    ListView mListView;
    private List<String> mData = new ArrayList<String>();
    private ArrayList<String> tidData = new ArrayList<String>();
    private ListViewAdapter mAdapter;
//    Handler handler = new Handler();
    private Group group;

//    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkvote);

        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");

        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_list_view_frame);
        mListView = (ListView) this.findViewById(R.id.test_list_view);
        initData();
        getVoteTheme();
    }

    private void initData() {

        mAdapter = new ListViewAdapter(this, mData);
        mListView.setAdapter(mAdapter);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                page = 0;
                mData.clear();
                getVoteTheme();
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
                Toast.makeText(CheckVoteActivity.this, "load more complete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ListViewAdapter extends BaseAdapter implements View.OnClickListener {
        private List<String> datas;
        private LayoutInflater inflater;

        public ListViewAdapter(Context context, List<String> data) {
            super();
            inflater = LayoutInflater.from(context);
            datas = data;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.vote_listitem_layout, parent, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(datas.get(position));

            convertView.setOnClickListener(this);
            convertView.setTag(position);
            return convertView;
        }

        public List<String> getData() {
            return datas;
        }

        @Override
        public void onClick(View view) {
            int TID = Integer.parseInt(tidData.get((int)view.getTag()));
            //待修改
            Intent intent = new Intent(CheckVoteActivity.this, VoteActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("group", group);
//            bundle.putInt("TID",TID);
//            intent.putExtras(bundle);
            intent.putExtra("ThemeName",mData.get((int)view.getTag()));
            intent.putExtra("TID",TID);
            startActivity(intent);
        }
    }


    //获取小组相关的投票主题
    private void getVoteTheme() {

        //用来取消请求
        String tag_string_req = "req_check_theme";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHECKVOTETHEME, new Response.Listener<String>() {

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

                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.refreshComplete();
                        ptrClassicFrameLayout.setLoadMoreEnable(true);
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
//            User user = new User();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
//                {
//                    "TID": "15",
//                        "GID": "18",
//                        "ThemeName": "qwrafwerw"
//                }
                mData.add(jsonObject.getString("ThemeName"));
                tidData.add(jsonObject.getString("TID"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
        ptrClassicFrameLayout.refreshComplete();
        ptrClassicFrameLayout.setLoadMoreEnable(true);
    }

}



