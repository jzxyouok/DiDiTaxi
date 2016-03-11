package com.cuit.diditaxi.receiver;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cuit.diditaxi.activity.DriverOrderDetailActivity;
import com.cuit.diditaxi.activity.PassengerDriverDetailActivity;
import com.cuit.diditaxi.model.SerializableMap;

import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

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

                TextContent text = (TextContent) msg.getContent();
                UserInfo userInfo = msg.getFromUser();
                String username = userInfo.getUserName();
                Map<String, String> extraMap;
                extraMap=text.getStringExtras();
                SerializableMap map = new SerializableMap();
                map.setMap(extraMap);

                if (extraMap.get("flag").equals("lookForCar")){
                    //司机跳转到订单详情界面
                    intent.setClass(mContext, DriverOrderDetailActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.putExtra("username",username);
                    intent.putExtra("order", map);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }else if (extraMap.get("flag").equals("driverAcceptOrder")){
                    //乘客跳转到司机详情界面
                    intent.setClass(mContext, PassengerDriverDetailActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            }
        }
    }

}
