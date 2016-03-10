package com.cuit.diditaxi.activity;

import android.os.Bundle;

import com.amap.api.services.core.LatLonPoint;
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.SerializableMap;

import java.util.HashMap;
import java.util.Map;

public class DriverOrderDetailActivity extends BaseActivity {

    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle.get("order") != null) {
            SerializableMap map = (SerializableMap) bundle.get("order");
            Map<String, String> extraMap = new HashMap<>();
            if (map != null) {
                extraMap = map.getMap();
            }
            mStartPoint = new LatLonPoint(Double.valueOf(extraMap.get("StartLat")), Double.valueOf(extraMap.get("StartLong")));
            mEndPoint = new LatLonPoint(Double.valueOf(extraMap.get("EndLat")), Double.valueOf(extraMap.get("EndLong")));
            showToastLong("Start".concat(mStartPoint.toString()));
            showToastLong("End".concat(mEndPoint.toString()));
        }
    }
}
