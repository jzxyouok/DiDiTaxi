package com.cuit.diditaxi.receiver;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

public class NotificationClickEventReceiver {
    private static final String TAG = NotificationClickEventReceiver.class.getSimpleName();

    private Context mContext;

    public NotificationClickEventReceiver(Context context) {
        mContext = context;
        JMessageClient.registerEventReceiver(this);
    }

    public void onEvent(NotificationClickEvent notificationClickEvent) {
        Log.d(TAG, "[onEvent] NotificationClickEvent !!!!");
        if (null == notificationClickEvent) {
            Log.w(TAG, "[onNotificationClick] message is null");
            return;
        }
        Message msg = notificationClickEvent.getMessage();
        if (msg != null){
            String targetID = msg.getTargetID();
            ConversationType type = msg.getTargetType();
            Conversation conv;
            if (type.equals(ConversationType.single)){
                conv = JMessageClient.getSingleConversation(targetID);
            }else conv = JMessageClient.getGroupConversation(Long.parseLong(targetID));
            conv.resetUnreadCount();
            Log.d("Notification", "Conversation unread msg reset");
            //跳转到消息显示界面
            Intent intent = new Intent();
            if (msg.getContentType().equals(ContentType.text)){

            }
        }
    }

}
