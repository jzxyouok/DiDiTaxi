package com.cuit.diditaxi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Administrator on 2016/3/6.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        JMessageClient.registerEventReceiver(this);
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }


    /**
     * 接收消息,有本地会话记录
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.group) {
            //群聊,有本地会话记录
            long groupID = ((GroupInfo) msg.getTargetInfo()).getGroupID();
            Conversation conv = JMessageClient.getGroupConversation(groupID);
            if (conv != null) {
                //TODO
            }
        } else {
            //单聊，有本地会话记录
            UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            final String userName = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(userName);
            if (conv != null) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                    }
                });
            }
        }
    }

    public void showToastShort(String content){
        Toast.makeText(getApplicationContext(),content,Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String content){
        Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();
    }
}
