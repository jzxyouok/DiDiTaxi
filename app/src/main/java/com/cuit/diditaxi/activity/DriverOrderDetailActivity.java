package com.cuit.diditaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.Event;
import com.cuit.diditaxi.model.Order;
import com.cuit.diditaxi.model.SerializableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;

public class DriverOrderDetailActivity extends BaseActivity {

    private Order mOrder;

    private String mPassengerUsername;

    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
//    private AMap mAMap;

    //------地理编码----
    private GeocodeSearch mStartGeocodeSearch;
    private GeocodeSearch mEndGeocodeSearch;

    @Bind(R.id.tv_driver_order_detail_start)
    TextView mTvStart;

    @Bind(R.id.tv_driver_order_detail_destination)
    TextView mTvEnd;

    @Bind(R.id.btn_driver_confirm_order)
    Button mBtnConfirmOrder;

    @OnClick(R.id.btn_driver_confirm_order)
    void confirmOrder() {

        if (!TextUtils.isEmpty(mTvStart.getText()) && !TextUtils.isEmpty(mTvEnd.getText())) {

            if (mOrder != null) {
                if (!mOrder.getIsAccepted()) {
                    //通知乘客，接单司机
                    JMessageClient.getUserInfo(mPassengerUsername, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                Conversation conversation = Conversation.createSingleConversation(userInfo.getUserName());

                                TextContent textContent = new TextContent("已接单");
                                Map<String, String> extraMap = new HashMap<>();
                                extraMap.put("flag", "driverAcceptOrder");
                                textContent.setExtras(extraMap);

                                final Message message = conversation.createSendMessage(textContent);
                                message.setOnSendCompleteCallback(new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {

                                            //更新Order信息，设为不可再接
                                            mOrder.setIsAccepted(true);
                                            mOrder.update(getApplicationContext(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Intent intent = new Intent(DriverOrderDetailActivity.this, DriverMainActivity.class);
                                                    intent.putExtra("flag", "orderDetail");
                                                    intent.putExtra("start", mStartPoint);
                                                    intent.putExtra("end", mEndPoint);
                                                    intent.putExtra("passenger", mPassengerUsername);

                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    showToastLong("订单暂时不可接取");
                                                }
                                            });


                                        }
                                    }
                                });
                                JMessageClient.sendMessage(message);
                                EventBus.getDefault().post(new Event.MessageEvent(message));
                            }
                        }
                    });
                } else {
                    showToastLong("此订单已被接取");
                }
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order_detail);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle.get("order") != null) {
            mPassengerUsername = (String) bundle.get("username");
            SerializableMap map = (SerializableMap) bundle.get("order");
            Map<String, String> extraMap = new HashMap<>();
            if (map != null) {
                extraMap = map.getMap();
            }
            mStartPoint = new LatLonPoint(Double.valueOf(extraMap.get("StartLat")), Double.valueOf(extraMap.get("StartLong")));
            mEndPoint = new LatLonPoint(Double.valueOf(extraMap.get("EndLat")), Double.valueOf(extraMap.get("EndLong")));
            String createTime = extraMap.get("createTime");
            //BMOB，查询Order
            BmobQuery<Order> andQuery = new BmobQuery<>();
            List<BmobQuery<Order>> queryList = new ArrayList<>();
            BmobQuery<Order> query1 = new BmobQuery<>();
            query1.addWhereEqualTo("passenger", mPassengerUsername);
            BmobQuery<Order> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("createTime", createTime);
            queryList.add(query1);
            queryList.add(query2);

            andQuery.and(queryList);
            andQuery.findObjects(getApplicationContext(), new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {

                    if (list.size() > 0) {
                        mOrder = list.get(0);
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }

        if (mStartPoint != null && mEndPoint != null) {

            mBtnConfirmOrder.setEnabled(true);

            mStartGeocodeSearch = new GeocodeSearch(getApplicationContext());
            mStartGeocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    //坐标 → 地址
                    if (i == 0) {
                        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                            mTvStart.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                        } else {
                            mTvStart.setText("没有找到地址信息");
                        }
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });

            mEndGeocodeSearch = new GeocodeSearch(getApplicationContext());
            mEndGeocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                    //坐标 → 地址
                    if (i == 0) {
                        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                            mTvEnd.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                        } else {
                            mTvStart.setText("没有找到地址信息");
                        }
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
        }

        //地理编码，坐标 → 地址
        RegeocodeQuery startRegeocode = new RegeocodeQuery(mStartPoint, 50, GeocodeSearch.AMAP);
        mStartGeocodeSearch.getFromLocationAsyn(startRegeocode);
        RegeocodeQuery endRegeocode = new RegeocodeQuery(mEndPoint, 50, GeocodeSearch.AMAP);
        mEndGeocodeSearch.getFromLocationAsyn(endRegeocode);

    }
}
