package com.cczq.missionforce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cczq.missionforce.fragment.GroupFragment;
import com.cczq.missionforce.fragment.MissionFragment;
import com.cczq.missionforce.fragment.PersonalFragment;
import com.cczq.missionforce.fragment.SettingsFragment;
import com.cczq.missionforce.loginresgister.LoginActivity;
import com.cczq.missionforce.loginresgister.utils.SessionManager;
import com.cczq.missionforce.utils.SQLiteHandler;
import com.cczq.missionforce.widget.CanaroTextView;
import com.yalantis.guillotine.animation.GuillotineAnimation;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long RIPPLE_DURATION = 250;
    private SQLiteHandler db;
    public SessionManager session;
    private GuillotineAnimation guillotineAnimation;
    private FragmentManager fragmentManager;
    private Menu mMenu;

//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.root)
    FrameLayout root;
//    @BindView(R.id.content_hamburger)
    View contentHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        root = (FrameLayout)findViewById(R.id.root);
        contentHamburger = (View)findViewById(R.id.content_hamburger);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        //创建碎片管理器
        fragmentManager = getSupportFragmentManager();

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        if (findViewById(R.id.fragment_container) != null) {

            //如果savedInstanceState不为空，说明只是从先前状态恢复，不必做任何工作直接返回
            if (savedInstanceState != null) {
                return;
            }
        }

        //创建Fragmen
        GroupFragment firstFragment = new GroupFragment();
        //取Intent的附加数据作为fragment构造函数的参数
        firstFragment.setArguments(getIntent().getExtras());
        //把fragment嵌入到容器
        fragmentManager.beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        ((TextView) findViewById(R.id.titleBar)).setText("小组");

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        //GuillotineBuilder的第一个参数为菜单的View,
        //第二个参数为关闭菜单的View也就是菜单布局中的按钮,
        //第三个参数为打开菜单的View也就是主页面中的按钮
        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
        guillotineMenu.setOnClickListener(this);

        findViewById(R.id.personal_group).setOnClickListener(this);
        findViewById(R.id.group_group).setOnClickListener(this);
        findViewById(R.id.mission_group).setOnClickListener(this);
        findViewById(R.id.settings_group).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        jumpToTheFragment(v.getId());
    }

    /**
     * @param i 通过点击的资源id来跳转到想要的fragement
     */
    private void jumpToTheFragment(int i) {
        //    hiddenMenu();
        clearSelect();
        Fragment jumpToFragment = null;
        switch (i) {
            case R.id.personal_group:
                Log.d("jump to personal_group","debug");
                ((TextView) findViewById(R.id.titleBar)).setText("个人资料");
                ((CanaroTextView) findViewById(R.id.personal)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new PersonalFragment();

                break;
            case R.id.group_group:
                Log.d("jump to group_group","debug");
                ((TextView) findViewById(R.id.titleBar)).setText("小组");
                ((CanaroTextView) findViewById(R.id.group)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new GroupFragment();

                break;
            case R.id.mission_group:

                Log.d("jump to mission_group","debug");
                ((TextView) findViewById(R.id.titleBar)).setText("任务");
                ((CanaroTextView) findViewById(R.id.mission)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new MissionFragment();

                break;
            case R.id.settings_group:
                Log.d("jump to setting_group","debug");
                ((TextView) findViewById(R.id.titleBar)).setText("设置");
                ((CanaroTextView) findViewById(R.id.settings)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new SettingsFragment();

                break;
            default:
                guillotineAnimation.close();
                return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //文章fragment--newFragment完全取代容器上的一个fragment
        transaction.replace(R.id.fragment_container, jumpToFragment);
        //增加一个返回堆栈，用户可以导航回来
        //transaction.addToBackStack(null);
        //提交事务
        transaction.commit();
        //关闭guillotineAnimation
        guillotineAnimation.close();
    }

    /**
     * 清除相应的选择
     */
    private void clearSelect() {
        ((CanaroTextView) findViewById(R.id.personal)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.group)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.mission)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.settings)).setTextAppearance(this, R.style.TextView_GuillotineItem);
    }

    public void logoutUser() {
        session.setLogin(false, session.UID());
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
