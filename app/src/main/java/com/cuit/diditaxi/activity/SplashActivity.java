package com.cuit.diditaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.cuit.diditaxi.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseActivity {

    @Bind(R.id.btn_splash_passenger)
    Button mBtnPassenger;

    @Bind(R.id.btn_splash_driver)
    Button mBtnDriver;

    @OnClick(R.id.btn_splash_passenger)
    void passenger(){
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this,PassengerLoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_splash_driver)
    void driver(){
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this,DriverLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        Bmob.initialize(getApplicationContext(),"89504b8e549c3d06b1b1c3370f778f53");

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
