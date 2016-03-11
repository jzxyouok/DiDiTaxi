package com.cuit.diditaxi.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/3/7.
 */
public class User extends BmobUser {

    private Boolean isDriver;
    private String nickName;
    //司机车牌号
    private String carNumber;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(Boolean isDriver) {
        this.isDriver = isDriver;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "isDriver=" + isDriver +
                ", nickName='" + nickName + '\'' +
                ", carNumber='" + carNumber + '\'' +
                '}';
    }
}
