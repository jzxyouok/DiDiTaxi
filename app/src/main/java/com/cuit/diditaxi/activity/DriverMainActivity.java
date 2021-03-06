package com.cuit.diditaxi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.adapter.PassengerOptionAdapter;
import com.cuit.diditaxi.model.Event;
import com.cuit.diditaxi.view.ListRecyclerViewDivider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;

public class DriverMainActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    //------定位-----
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private String mCity;
    //定位结果，坐标
    private LatLng mLocateLatLng = null;

    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;

    private String mPassenger;

    @Bind(R.id.drawer_driver_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.layout_driver_main_left_menu)
    LinearLayout mLayoutLeftMenu;

    @Bind(R.id.rv_driver_main_left_menu)
    RecyclerView mRecyclerView;
    private List<String> mOptionList;
    private PassengerOptionAdapter mOptionAdapter;

    @Bind(R.id.toolbar_driver_main)
    Toolbar mToolbar;

    @Bind(R.id.map_view_driver_main)
    MapView mMapView;

    private AMap mAMap;

    @Bind(R.id.tv_driver_main_tip)
    TextView mTvTip;

    @Bind(R.id.btn_driver_main_found_passenger)
    Button mBtnFoundPassenger;

    @Bind(R.id.layout_driver_main_tip)
    LinearLayout mLayoutTip;

    @OnClick(R.id.btn_driver_main_found_passenger)
    void foundPassenger() {

        if (mBtnFoundPassenger.getText().equals("已接到乘客")) {

            //发送通知，通知乘客行程开始
            if (mPassenger != null) {
                JMessageClient.getUserInfo(mPassenger, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i == 0) {

                            Conversation conversation = Conversation.createSingleConversation(userInfo.getUserName());

                            TextContent textContent = new TextContent("行程开始");
                            Map<String, String> extraMap = new HashMap<>();
                            extraMap.put("flag", "start");
                            extraMap.put("startLat", String.valueOf(mStartPoint.getLatitude()));
                            extraMap.put("startLon",String.valueOf(mStartPoint.getLongitude()));
                            extraMap.put("endLat", String.valueOf(mEndPoint.getLatitude()));
                            extraMap.put("endLon",String.valueOf(mEndPoint.getLongitude()));
                            textContent.setExtras(extraMap);

                            Message message = conversation.createSendMessage(textContent);
                            message.setOnSendCompleteCallback(new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        mTvTip.setText("已接到乘客，请前往乘客目的地");
                                        mBtnFoundPassenger.setText("已到达目的地");
                                    }
                                }
                            });
                            JMessageClient.sendMessage(message);
                            EventBus.getDefault().post(new Event.MessageEvent(message));
                        }
                    }
                });
            }

        } else if (mBtnFoundPassenger.getText().equals("已到达目的地")) {
            //发送通知，通知乘客已抵达目的地
            if (mPassenger != null) {
                JMessageClient.getUserInfo(mPassenger, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i == 0) {

                            Conversation conversation = Conversation.createSingleConversation(userInfo.getUserName());

                            TextContent textContent = new TextContent("行程结束");
                            Map<String, String> extraMap = new HashMap<>();
                            extraMap.put("flag", "end");
                            textContent.setExtras(extraMap);

                            Message message = conversation.createSendMessage(textContent);
                            message.setOnSendCompleteCallback(new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        mTvTip.setText("已到达乘客目的地");
                                        mBtnFoundPassenger.setVisibility(View.GONE);
                                    }
                                }
                            });
                            JMessageClient.sendMessage(message);
                            EventBus.getDefault().post(new Event.MessageEvent(message));
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        ButterKnife.bind(this);

        //显示地图
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();

            setupMap();
        }

        //Toolbar
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.default_avatar_me);
        setSupportActionBar(mToolbar);

        //DrawerLayout
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //禁止DrawerLayout通过手势滑出
        //打开滑动手势 LOCK_MODE_UNLOCKED
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //Option
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ListRecyclerViewDivider itemDivider = new ListRecyclerViewDivider(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDivider);

        mOptionList = new ArrayList<>();
        mOptionList.add("退出登录");
        mOptionAdapter = new PassengerOptionAdapter(DriverMainActivity.this, mOptionList);
        mRecyclerView.setAdapter(mOptionAdapter);
        //OptionItem点击监听
        mOptionAdapter.setOnItemClickListener(new PassengerOptionAdapter.OnItemClickListener() {
            @Override
            public void itemLongClick(View view, int position) {

                String option = mOptionList.get(position);
                if (option.equals("退出登录")) {
                    BmobUser.logOut(DriverMainActivity.this);
                    JMessageClient.logout();
                    Intent intent = new Intent(DriverMainActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //如果是由订单详情页面跳转过来
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String flag = (String) bundle.get("flag");
            if (flag != null && flag.equals("orderDetail")) {

                //显示TipLayout
                mLayoutTip.setVisibility(View.VISIBLE);

                mStartPoint = (LatLonPoint) bundle.get("start");
                mEndPoint = (LatLonPoint) bundle.get("end");
                mPassenger = bundle.getString("passenger");

                //在地图上显示驾车路径
                RouteSearch routeSearch = new RouteSearch(getApplicationContext());
                RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
                RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
                routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
                routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

                        if (i == 0) {
                            DrivePath drivePath = driveRouteResult.getPaths().get(0);
                            mAMap.clear();
                            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(getApplicationContext(), mAMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
                            drivingRouteOverlay.setNodeIconVisibility(false);
                            drivingRouteOverlay.addToMap();
                            drivingRouteOverlay.zoomToSpan();
                        }
                    }

                    @Override
                    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                    }
                });


            }
        }

    }

    private void setupMap() {

        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        //地图上的控件
        UiSettings uiSettings = mAMap.getUiSettings();
        //缩放控件，默认true
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        //定位按钮,默认false
        uiSettings.setMyLocationButtonEnabled(true);
        //地图旋转，默认true
        uiSettings.setRotateGesturesEnabled(false);
        //指南针,默认false
        uiSettings.setCompassEnabled(true);

        /**
         * 自定义定位结果显示突变
         *
         */
        MyLocationStyle locationStyle = new MyLocationStyle();
        //图标
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
        //边框颜色
        // 设置圆形的边框颜色
        locationStyle.strokeColor(Color.TRANSPARENT);
        //边框宽度
        locationStyle.strokeWidth(0);
        //填充色
        locationStyle.radiusFillColor(Color.TRANSPARENT);
        mAMap.setMyLocationStyle(locationStyle);

        //设置定位资源。如果不设置此定位资源则定位按钮不可点击
        mAMap.setLocationSource(this);

        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationEnabled(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        deactivate();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerLayout.openDrawer(mLayoutLeftMenu);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mLocationChangedListener = onLocationChangedListener;
        if (mLocationClient == null) {

            //1.初始化定位客户端，设置监听
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);

            //2.配置定位参数，启动定位
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(200000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);

            //3.启动定位
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

        mLocationChangedListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null && mLocationChangedListener != null) {

            if (aMapLocation.getErrorCode() == 0) {
                //定位成功

                mLocationChangedListener.onLocationChanged(aMapLocation);
                //定位到的位置
                mLocateLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mCity = aMapLocation.getCity();


                //Camera移动到定位位置
                CameraPosition position = new CameraPosition(mLocateLatLng, 16, 0, 0);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
                mAMap.animateCamera(cameraUpdate, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }
        }
    }
}
