package com.cuit.diditaxi.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/2.
 */
public class SerializableMap implements Serializable {

    private Map<String,String> mMap;

    public Map<String, String> getMap() {
        return mMap;
    }

    public void setMap(Map<String, String> map) {
        mMap = map;
    }
}
