package com.cuit.diditaxi.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Administrator on 2016/3/9.
 */
public class NumberUtil {

    /**
     * 米换算成千米
     */
    public static String meterToKm(float meter) {

        meter = meter / 1000;
        BigDecimal bigDecimal = new BigDecimal(meter);
        double result = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return String.valueOf(result).concat("公里");
    }

    /**
     * 四舍五入，不保留小数
     */
    public static String roundHalfUp(double number) {

        BigDecimal bigDecimal = new BigDecimal(number);
        int result = bigDecimal.setScale(0, RoundingMode.HALF_UP).intValue();

        return String.valueOf(result);
    }
}
