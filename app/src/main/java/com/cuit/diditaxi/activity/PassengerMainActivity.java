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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.adapter.PassengerOptionAdapter;
import com.cuit.diditaxi.model.CloudMarkerOverlay;
import com.cuit.diditaxi.model.Event;
import com.cuit.diditaxi.model.SerializableMap;
import com.cuit.diditaxi.utils.NumberUtil;
import com.cuit.diditaxi.utils.TimeUtil;
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
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;

public class PassengerMainActivity extends BaseActivity implements LocationSource, AMapLocationListener {


    //------JMessage-----
    private long mGroupId;
    private Conversation mConversation;


    //------地图-----
    private AMap mAMap;
    @Bind(R.id.map_view_passenger_main)
    MapView mMapView;

    //------定位-----
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private String mCity;
    //定位结果，坐标
    private LatLng mLocateLatLng = null;
    //出发地坐标
    private LatLonPoint mStartLatLonPoint = null;
    //目的地坐标
    private LatLonPoint mEndLatLonPoint = null;

    //------地理编码----
    private GeocodeSearch mGeocodeSearch;

    //------云图-------
    private CloudSearch mCloudSearch;
    private CloudSearch.Query mCloudQuery;
    //云图TableId
    private String mCloudMapTableId = "56c6c7df305a2a3288157973";
    //自定义CloudMarker
    private CloudMarkerOverlay mCloudMarkerOverlay;
    private List<CloudItem> mCloudItemList;
    private Marker mCloudMarker;

    //-------------Marker----------
    //上车位置Marker
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;

    private boolean isFirstIn = true;
    private boolean isStopUpdateStart = false;

    @Bind(R.id.tv_passenger_cost)
    TextView mTvCost;

    @Bind(R.id.drawer_passenger_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.layout_passenger_main_left_menu)
    LinearLayout mLayoutLeftMenu;

    @Bind(R.id.rv_passenger_main_left_menu)
    RecyclerView mRecyclerView;
    private List<String> mOptionList;
    private PassengerOptionAdapter mOptionAdapter;

    @Bind(R.id.toolbar_passenger_main)
    Toolbar mToolbar;

    @Bind(R.id.tv_passenger_start)
    TextView mTvStart;

    @Bind(R.id.tv_passenger_destination)
    TextView mTvDestination;

    @Bind(R.id.btn_look_for_car)
    Button mBtnLookForCar;

    @Bind(R.id.layout_passenger_main_tip)
    LinearLayout mLayoutTip;

    @OnClick(R.id.btn_look_for_car)
    void lookForCar() {
        setupMap();

        //发送消息
        TextContent text = new TextContent("点击查看详情");
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("flag", "lookForCar");
        extraMap.put("StartLat", String.valueOf(mStartLatLonPoint.getLatitude()));
        extraMap.put("StartLong", String.valueOf(mStartLatLonPoint.getLongitude()));
        extraMap.put("EndLat", String.valueOf(mEndLatLonPoint.getLatitude()));
        extraMap.put("EndLong", String.valueOf(mEndLatLonPoint.getLongitude()));
        text.setExtras(extraMap);

        Message textMsg = mConversation.createSendMessage(text);
        textMsg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    showToastShort("正在寻找车辆，请稍后...");
                }
            }
        });
        JMessageClient.sendMessage(textMsg);
        EventBus.getDefault().post(new Event.MessageEvent(textMsg));
    }

    @OnClick(R.id.tv_passenger_start)
    void start() {

    }

    @OnClick(R.id.tv_passenger_destination)
    void destination() {

        //选择目的地
        if (mCity != null) {
            Intent intent = new Intent(PassengerMainActivity.this, PassengerSelectEndActivity.class);
            intent.putExtra("city", mCity);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);

        ButterKnife.bind(this);

        //Option
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ListRecyclerViewDivider itemDivider = new ListRecyclerViewDivider(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDivider);

        mOptionList = new ArrayList<>();
        mOptionList.add("退出登录");
        mOptionAdapter = new PassengerOptionAdapter(PassengerMainActivity.this, mOptionList);
        mRecyclerView.setAdapter(mOptionAdapter);
        //OptionItem点击监听
        mOptionAdapter.setOnItemClickListener(new PassengerOptionAdapter.OnItemClickListener() {
            @Override
            public void itemLongClick(View view, int position) {

                String option = mOptionList.get(position);
                if (option.equals("退出登录")) {
                    BmobUser.logOut(PassengerMainActivity.this);
                    JMessageClient.logout();
                    Intent intent = new Intent(PassengerMainActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

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

        //地图
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();

            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().get("route") != null) {

                    //隐藏TipLayout
                    mLayoutTip.setVisibility(View.INVISIBLE);
                    //开始行程，绘制驾车路径
                    SerializableMap map = (SerializableMap) getIntent().getExtras().get("route");
                    Map<String, String> mMap = null;
                    if (map != null) {
                        mMap = map.getMap();
                    }

                    if (mMap != null) {
                        LatLonPoint startPoint = new LatLonPoint(Double.valueOf(mMap.get("startLat")), Double.valueOf(mMap.get("startLon")));
                        LatLonPoint endPoint = new LatLonPoint(Double.valueOf(mMap.get("endLat")), Double.valueOf(mMap.get("endLon")));

                        RouteSearch routeSearch = new RouteSearch(getApplicationContext());
                        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
                        RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
                        routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
                        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                            @Override
                            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                            }

                            @Override
                            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

                                if (i == 0) {
                                    if (driveRouteResult != null && driveRouteResult.getPaths().size() > 0) {
                                        DrivePath drivePath = driveRouteResult.getPaths().get(0);
                                        mAMap.clear();
                                        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(getApplicationContext(), mAMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
                                        drivingRouteOverlay.setNodeIconVisibility(false);
//                                    drivingRouteOverlay.removeFromMap();
                                        drivingRouteOverlay.addToMap();
                                        drivingRouteOverlay.zoomToSpan();
                                    }
                                }
                            }

                            @Override
                            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                            }
                        });
                    }
                }
            } else {
                setupMap();
            }
        }

        //禁止DrawerLayout通过手势滑出
        //打开滑动手势 LOCK_MODE_UNLOCKED
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setupMap() {

        //地图移动监听
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (!isFirstIn && !isStopUpdateStart) {
                    //上车位置Marker一直显示在地图中心
                    mMarker.setPosition(cameraPosition.target);
                    //地图移动时，隐藏InfoWindow
                    mMarker.hideInfoWindow();
                    //地图移动时，起点显示省略号
                    mTvStart.setText("......");
                }
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

                //地图停止移动，显示InfoWindow
                mMarker.showInfoWindow();
                //逆地理编码，坐标 → 地址，显示到界面上
                LatLonPoint point = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);

                //发送逆地理编码请求
                if (!isStopUpdateStart) {
                    RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.AMAP);
                    mGeocodeSearch.getFromLocationAsyn(query);

                    //更新上车点
                    if (mStartLatLonPoint == null) {
                        mStartLatLonPoint = new LatLonPoint(point.getLatitude(), point.getLongitude());
                    } else {
                        mStartLatLonPoint.setLatitude(point.getLatitude());
                        mStartLatLonPoint.setLongitude(point.getLongitude());
                    }
                }

            }
        });

        //设置Marker点击事件监听器
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //返回true,点击Marker，不会移动Camera
                return true;
            }
        });

        /**
         *
         *设置infoWindow点击事件监听器
         *
         */
        mAMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });

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
        //比例尺，默认false
//        uiSettings.setScaleControlsEnabled(true);

        /**
         * 自定义定位结果显示突变
         *
         */
        MyLocationStyle locationStyle = new MyLocationStyle();
        //图标
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_loaction));
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

        /**
         *
         *
         * 地理编码
         *
         */
        mGeocodeSearch = new GeocodeSearch(this);
        mGeocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                //坐标 → 地址
                if (i == 0) {
                    if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                        mTvStart.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress().concat("附近"));
                    } else {
                        mTvStart.setText("没有找到地址信息");
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                //地址 → 坐标
            }
        });

        /**
         *
         * 云图
         *
         */
        mCloudSearch = new CloudSearch(this);
        mCloudSearch.setOnCloudSearchListener(new CloudSearch.OnCloudSearchListener() {
            @Override
            public void onCloudSearched(CloudResult cloudResult, int i) {

                if (cloudResult != null) {
                    List<CloudItem> cloudItemList = cloudResult.getClouds();
                    if (cloudItemList.size() > 0) {
                        cloudItemList.get(0).getCloudImage().size();
                        mCloudMarkerOverlay = new CloudMarkerOverlay(cloudItemList, mAMap);
                        mCloudMarkerOverlay.addCloudMarkerToMap();

                        //从CloudItem中获取到周围司机用户名，使用JMessage发送群发信息，通知司机抢单
                        //CloudItem.getTitle
                        final List<String> driverList = new ArrayList<String>();
                        for (CloudItem cloudItem : cloudItemList) {
                            driverList.add(cloudItem.getTitle());
                        }

                        JMessageClient.createGroup("新订单通知!!!", "", new CreateGroupCallback() {
                            @Override
                            public void gotResult(int i, String s, long l) {

                                if (i == 0) {
                                    mGroupId = l;
                                    mConversation = Conversation.createGroupConversation(mGroupId);
                                    //添加司机名单到发送列表
                                    JMessageClient.addGroupMembers(mGroupId, driverList, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == 0) {
                                                mBtnLookForCar.setEnabled(true);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int i) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        deactivate();
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

                //设置云图显示的中心点，搜索范围
                LatLonPoint cloudPoint = new LatLonPoint(mLocateLatLng.latitude, mLocateLatLng.longitude);
                CloudSearch.SearchBound searchBound = new CloudSearch.SearchBound(cloudPoint, 4000);
                //将云图数据显示到地图上
                try {
                    mCloudQuery = new CloudSearch.Query(mCloudMapTableId, "", searchBound);
                    mCloudSearch.searchCloudAsyn(mCloudQuery);
                } catch (AMapException e) {
                    e.printStackTrace();
                    showToastLong(e.getMessage());
                }

                //Camera移动到定位位置
                CameraPosition position = new CameraPosition(mLocateLatLng, 16, 0, 0);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
                if (isFirstIn) {
                    mAMap.animateCamera(cameraUpdate, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            isFirstIn = false;

                            //显示上车位置Marker
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.title("在这里上车");
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.passenger_pickup_point));
                            mMarker = mAMap.addMarker(markerOptions);
                            mMarker.setPosition(mLocateLatLng);
                            mMarker.showInfoWindow();

                            mTvStart.setText(mLocateLatLng.toString());
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            Bundle bundle = data.getExtras();
            Tip tip = bundle.getParcelable("tip");
            if (tip != null) {
                mTvDestination.setText(tip.getName());
                mEndLatLonPoint = tip.getPoint();

                //驾车路径规划
                if (mEndLatLonPoint != null) {
                    RouteSearch routeSearch = new RouteSearch(PassengerMainActivity.this);
                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartLatLonPoint, mEndLatLonPoint);
                    RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
                    routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
                    routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                        @Override
                        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                        }

                        @Override
                        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

                            if (i == 0) {
                                if (driveRouteResult != null && driveRouteResult.getPaths().size() > 0) {
                                    //停止更新上车地点
                                    isStopUpdateStart = true;

                                    DrivePath drivePath = driveRouteResult.getPaths().get(0);
                                    //路段列表
                                    List<DriveStep> driveStepList = drivePath.getSteps();
                                    //遍历driveStepList，得到驾车距离，预估用时
                                    float totalDistance = 0.0f;
                                    float totalDuration = 0.0f;
                                    for (DriveStep driverStep : driveStepList) {
                                        totalDistance = totalDistance + driverStep.getDistance();
                                        totalDuration = totalDuration + driverStep.getDuration();
                                    }

                                    mTvCost.setVisibility(View.VISIBLE);
                                    //1米，0.005
                                    mTvCost.setText("预计费用:".concat(NumberUtil.roundHalfUp(totalDistance * 0.005)).concat("元  距离:").concat(NumberUtil.meterToKm(totalDistance)).concat("  预计用时:").concat(TimeUtil.formatSecond(totalDuration)));
                                    mBtnLookForCar.setVisibility(View.VISIBLE);

                                    mAMap.clear();
                                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(PassengerMainActivity.this, mAMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
                                    drivingRouteOverlay.setNodeIconVisibility(false);
//                                    drivingRouteOverlay.removeFromMap();
                                    drivingRouteOverlay.addToMap();
                                    drivingRouteOverlay.zoomToSpan();
                                }
                            }
                        }

                        @Override
                        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                        }
                    });
                }
            }
        }
    }
}
