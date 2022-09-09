package com.kcb.pay.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private  final static DateUtils DATA = new DateUtils();

    private DateUtils(){

    }

    public static DateUtils getDateUtils(){
        return DATA;
    }

    public static String getStringDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /*将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss */
    public static String dateToStrLong(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /*将长时间格式时间转换为字符串 yyyy-MM-dd */
    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }
}
