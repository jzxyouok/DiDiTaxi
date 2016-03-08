package com.cuit.diditaxi.model;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;
import com.cuit.diditaxi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 */
public class CloudMarkerOverlay {

    private List<CloudItem> mCloudItemList;
    private AMap mAMap;
    private List<Marker> mMarkerList = new ArrayList<Marker>();

    public CloudMarkerOverlay(List<CloudItem> cloudItemList, AMap AMap) {
        mCloudItemList = cloudItemList;
        mAMap = AMap;
    }

    public void addCloudMarkerToMap(){
        for (int i = 0; i < mCloudItemList.size(); i++) {
            Marker marker = mAMap.addMarker(getMarkerOptions(i));
            marker.setObject(i);
            mMarkerList.add(marker);
        }
    }

    private MarkerOptions getMarkerOptions(int i) {

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(mCloudItemList.get(i).getLatLonPoint().getLatitude(),mCloudItemList.get(i).getLatLonPoint().getLongitude());
        markerOptions.position(latLng);
//        markerOptions.title(mCloudItemList.get(i).getTitle());
//        markerOptions.snippet(mCloudItemList.get(i).getSnippet());
//        markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));

        return markerOptions;
    }
}
