package com.zty.hqx.util;

import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ZtyUtil {
    private static final int min = 10;
    private static final int max = 99;
    private static final Calendar cal = Calendar.getInstance();
    private static final Random random = new Random();
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//    @Value("${hqx.staticUrl}")
    private static final String staticUrl = "http://49.4.114.114:81/hqx_static/";

//    @Value("${hqx.absoluteStaticUrl}")
    private static final String absoluteUrl = "/funyoo_project/hqx_app/hqx_static/";

    //生成名字
    public static String creatName(String orgName) {
        String newName = getRandomName();
        int index = orgName.indexOf(".");
        if (index <= 0) return null;
        String type = orgName.substring(index);
        return newName + type;
    }

    //随机生成名字：当前时间+两位随机数
    public static String getRandomName() {
        String time = String.valueOf(System.currentTimeMillis());
        int num = random.nextInt(max) % (max - min + 1) + min;
        return time + num;
    }

    public static String dealSqlPathToFile(String path) {
        return absoluteUrl + path.substring(staticUrl.length(), path.length());
    }

    public static String getLastWeek(){
        return getLastWeek(new Date());
    }

    public static String getLastWeek(Date date){
        cal.setTime(date);
        cal.add(Calendar.DATE, - 7);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getLastWeek(String str) throws ParseException {
        cal.setTime(df.parse(str));
        cal.add(Calendar.DATE, - 7);
        Date d = cal.getTime();
        return df.format(d);
    }


    public static String getYesterday(){
        return getYesterday(new Date());
    }

    public static String getYesterday(Date date){
        cal.setTime(date);
        cal.add(Calendar.DATE, - 1);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getLastMonth(){
        return getLastMonth(new Date());
    }

    public static String getLastMonth(Date date){
        cal.setTime(date);
        cal.add(Calendar.MONTH, - 1);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getLastMonth(String str) throws ParseException {
        cal.setTime(df.parse(str));
        cal.add(Calendar.MONTH, - 1);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getLastYear(){
        return getLastYear(new Date());
    }

    public static String getLastYear(Date date){
        cal.setTime(date);
        cal.add(Calendar.YEAR, - 1);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getLastYear(String str) throws ParseException {
        cal.setTime(df.parse(str));
        cal.add(Calendar.YEAR, - 1);
        Date d = cal.getTime();
        return df.format(d);
    }

    public static String getNowTime(){
        return df.format(new Date());
    }
}
