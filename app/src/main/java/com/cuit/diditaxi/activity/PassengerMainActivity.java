package com.cuit.diditaxi.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.CloudMarkerOverlay;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerMainActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    //------地图-----
    private AMap mAMap;
    @Bind(R.id.map_view_passenger_main)
    MapView mMapView;

    //------定位-----
    private LocationSource.OnLocationChangedListener mLocationChangedListener;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    //定位结果，坐标
    private LatLng mLocateLatLng = null;

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

    @Bind(R.id.drawer_passenger_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.layout_passenger_main_left_menu)
    LinearLayout mLayoutLeftMenu;

    @Bind(R.id.list_passenger_main_left_menu)
    ListView mListLeftMenu;

    @Bind(R.id.toolbar_passenger_main)
    Toolbar mToolbar;

    @Bind(R.id.tv_passenger_start)
    TextView mTvStart;

    @Bind(R.id.tv_passenger_destination)
    TextView mTvDestination;

    @OnClick(R.id.tv_passenger_start)
    void start() {

    }

    @OnClick(R.id.tv_passenger_destination)
    void destination() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);

        ButterKnife.bind(this);

        //Toolbar
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.default_avatar_me);

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

            setupMap();
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

                if (!isFirstIn) {
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
                RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.AMAP);
                //发送逆地理编码请求
                mGeocodeSearch.getFromLocationAsyn(query);
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
        uiSettings.setScaleControlsEnabled(true);

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
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
                //定位到的左边
                mLocateLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

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
                CameraPosition position = new CameraPosition(mLocateLatLng,17,0,0);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
                if (isFirstIn){
                    mAMap.animateCamera(cameraUpdate, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            isFirstIn=false;

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
}