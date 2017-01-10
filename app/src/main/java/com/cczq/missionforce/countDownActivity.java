package com.cczq.missionforce;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.missionforce.Model.Mission;
import com.cczq.missionforce.utils.configURL;
import com.john.waveview.WaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;
import io.feeeei.circleseekbar.CircleSeekBar;

/**
 * Created by Shyuan on 2016/10/13.
 */

public class countDownActivity extends Activity {


    private Mission mission;
    private CircleSeekBar mSecondSeekbar;
    private CircleSeekBar mMinuteSeekbar;
    private TextView missionNameTextView;
    private TextView missionDescriptionTextView;
    private WaveView waveView;
    private Button btnCommitMission;
    private int remainTime;
    private int remainPer;

    private countDownActivity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mSecondSeekbar = (CircleSeekBar) findViewById(R.id.seek_second);
        mMinuteSeekbar = (CircleSeekBar) findViewById(R.id.seek_minute);
        waveView = (WaveView) findViewById(R.id.wave_view);
        missionNameTextView = (TextView) findViewById(R.id.missionName);
        missionDescriptionTextView = (TextView) findViewById(R.id.missionDescription);
        btnCommitMission = (Button) findViewById(R.id.btn_commitMission);

        Intent intent = getIntent();
        mission = (Mission) intent.getSerializableExtra("mission");
        waveView.setProgress(1);
        remainPer = 0;
        missionNameTextView.setText(mission.missionNameText);
        missionDescriptionTextView.setText(mission.missionDescriptionText);

        final CountdownView mCvCountdownView = (CountdownView) findViewById(R.id.countdownView);
        //时间改变的回调函数  基本单位平均是10毫秒
        mCvCountdownView.setOnCountdownIntervalListener(10 * 60, new CountdownView.OnCountdownIntervalListener() {
            @Override
            public void onInterval(CountdownView cv, long remainTime) {
                double missionTime = mission.time * 1000 * 60;
                remainPer = (int) (((missionTime - remainTime) / missionTime) * 100);
                waveView.setProgress(remainPer);
                getActivity().remainTime =(int)(remainTime/60000);
            }
        });
        btnCommitMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitMission();
            }
        });


        start();
        mCvCountdownView.setTag("countDownView");
        mCvCountdownView.start(mission.time * 1000 * 60);
    }


    @Override
    protected  void onStop()
    {
        super.onStop();
        commitMission();
    }

    //使得两个秒盘和十秒盘转起来
    private void start() {
        //1秒钟的线程
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    mHandler.sendEmptyMessage(i);
                    if (i == 100)
                        i = 0;
                    SystemClock.sleep(10);
                }
            }
        }.start();

        //十秒钟的线程
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 1000; i++) {
                    mMinuteHandler.sendEmptyMessage(i);
                    if (i == 1000)
                        i = 0;
                    SystemClock.sleep(10);
                }
            }
        }.start();

    }

    //提交任务时间
    private void commitMission() {

        //用来取消请求
        String tag_string_req = "req_commit_mission";
        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_COMMITMISSION, new Response.Listener<String>() {

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
                    if (ret == 200 && code == 200 && msg.equals("任务时间更新成功")) {
                        finish();
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
                params.put("MID", Integer.toString(mission.MID));
                params.put("TIME", Integer.toString(remainTime));
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private Handler mMinuteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int value = msg.what;
            mMinuteSeekbar.setCurProcess(value);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int value = msg.what;
            mSecondSeekbar.setCurProcess(value);
        }
    };

}
