package com.example.labourtimesystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.labourtimesystem.common.BaseActivity;
import com.example.labourtimesystem.util.SPUtils;

public class LaunchActivity extends BaseActivity implements Animation.AnimationListener {

    private TextView textView = null;
    private Animation alphaAnimation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        fullScreen();
        textView = findViewById(R.id.tv_welcome);
        alphaAnimation = AnimationUtils.loadAnimation(this,
                R.anim.welcome_alpha);
        alphaAnimation.setFillEnabled(true);//启动Fill保持
        alphaAnimation.setFillAfter(true);//设置动画的最后一帧是保留在view上的
        textView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(this);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        //动画结束时结束欢迎页面并跳转到主页面
        isLoginUser();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public boolean onKeyDown(int KeyCode,KeyEvent event){
        //在欢迎页面屏蔽BACK键
        if(KeyCode== KeyEvent.KEYCODE_BACK){
            return false;
        }
        return false;
    }


    // 判断用户是否登录过的用户
    private void isLoginUser(){
        Object token = SPUtils.get(this, "token", "");
        // 判断token是否为空，若不为空，则代表为登录状态，直接跳转到对应的主页
        if(!token.equals("")){
            Intent intent = null;
            Object username = SPUtils.get(this, "username", "");
            UserHelp userHelp = UserHelp.getInstance();
            userHelp.setUsername((String)username);
            // 根据返回的username 身份 判断跳转的对应主页
            if(username.equals("一级权限测试")){
                intent = new Intent(this, TeacherLvOneMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }else if(username.equals("二级权限测试")){
                intent = new Intent(this, TeacherLvTwoMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }else if(username.equals("三级权限测试")){
                intent = new Intent(this, TeacherLvThreeMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }else{
                intent = new Intent(this, StudentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        }else{
            // token为空，未登录，则跳转到选择登录页面
            Log.i("TAG", "token: " + "null");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        }
    }

    private void fullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }
}