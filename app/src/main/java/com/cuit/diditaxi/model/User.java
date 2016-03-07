package com.cuit.diditaxi.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/3/7.
 */
public class User extends BmobUser {

    private Boolean isDriver;
    private String nickName;

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
}
