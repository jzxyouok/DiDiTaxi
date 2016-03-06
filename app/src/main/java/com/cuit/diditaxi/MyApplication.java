package com.cuit.diditaxi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.cuit.diditaxi.receiver.NotificationClickEventReceiver;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by Administrator on 2016/3/6.
 */
public class MyApplication extends Application {

    public static SharedPreferences mSP;
    private static final String JCHAT_CONFIGS = "JChat_configs";

    @Override
    public void onCreate() {
        super.onCreate();

        //定义Bmob缓存目录名称
        BmobConfiguration config = new BmobConfiguration.Builder(this).customExternalCacheDir("DiDi").build();
        BmobPro.getInstance(this).initConfig(config);
        //初始化JMessage
        JMessageClient.init(getApplicationContext());
        mSP = getApplicationContext().getSharedPreferences(JCHAT_CONFIGS, Context.MODE_PRIVATE);
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
        new NotificationClickEventReceiver(getApplicationContext());
    }
}
