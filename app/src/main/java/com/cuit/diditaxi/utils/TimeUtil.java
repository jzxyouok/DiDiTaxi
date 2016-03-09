package com.cuit.diditaxi.utils;

/**
 * Created by Administrator on 2016/3/9.
 */
public class TimeUtil {

    /**
     * 秒换算成时分
     *
     *
     */
    public static String formatSecond(float second){

        int minUnit = 60;
        int hourUnit = minUnit*60;

        int hour = (int) (second/hourUnit);
        int min = (int) ((second-hour*hourUnit)/minUnit);

        StringBuilder stringBuffer = new StringBuilder();
        if (hour>0){
            stringBuffer.append(hour).append("时");
        }else {
            stringBuffer.append("0时");
        }
        if (min>0){
            stringBuffer.append(min).append("分");
        }else stringBuffer.append("0分");

        return stringBuffer.toString();
    }
}
