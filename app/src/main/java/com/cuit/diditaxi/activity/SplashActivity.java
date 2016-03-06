package com.cuit.diditaxi.activity;

import android.os.Bundle;
import android.view.Window;

import com.cuit.diditaxi.R;

import cn.bmob.v3.Bmob;
import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        Bmob.initialize(getApplicationContext(),"89504b8e549c3d06b1b1c3370f778f53");

        //判断是否登录

    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }
}
