package com.cuit.diditaxi.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.cuit.diditaxi.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerMainActivity extends BaseActivity {

    private AMap mAMap;

    @Bind(R.id.drawer_passenger_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.layout_passenger_main_left_menu)
    LinearLayout mLayoutLeftMenu;

    @Bind(R.id.list_passenger_main_left_menu)
    ListView mListLeftMenu;

    @Bind(R.id.toolbar_passenger_main)
    Toolbar mToolbar;

    @Bind(R.id.map_view_passenger_main)
    MapView mMapView;

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

        //显示地图
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
    }
}
