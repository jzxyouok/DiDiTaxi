package com.cuit.diditaxi.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/3/12.
 */
public class Order extends BmobObject {

    private String passenger;
    private List<String> driverList;
    //订单是否已经被接
    private Boolean isAccepted;
    private String createTime;
    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public List<String> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<String> driverList) {
        this.driverList = driverList;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
