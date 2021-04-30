package com.bugull.hithium.core.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }
    /**
     * 一天的毫秒值
     * */
    public static final long ONE_DAY_MIL_SECONDS = 24 * 60 * 60 * 1000;

    /**
     * 七天的秒值
     * */
    public static final long ONE_DAY_MIN_SECONDS = 24 * 60 * 60 * 7;
    /**
    * 5分钟的秒数
    * */
    public static final long FIME_MIN_SECONDS = 5 * 60;

    public static final String FORMAT = "yyyy-MM-dd";

    private static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static ThreadLocal<SimpleDateFormat> sdfWithT = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
    };

    private static ThreadLocal<SimpleDateFormat> sdfWithHHmm = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };

    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = sdf.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static Date dateToStrWithT(String dateDate) throws ParseException {
        SimpleDateFormat formatter = sdfWithT.get();
        Date date = formatter.parse(dateDate);
        return date;
    }

    public static Date dateToStrWithHHmm(Date dateDate) throws ParseException {
        SimpleDateFormat formatter = sdfWithHHmm.get();
       return formatter.parse(formatter.format(dateDate));
    }
    public static Date dateToStrWithHHmmWith(String dateDateStr) throws ParseException {
        SimpleDateFormat formatter = sdfWithHHmm.get();
        return formatter.parse(dateDateStr);
    }

    /**
     * 获取日期的开始时间   2019-07-09 11：11：11  -> 2019-07-09 00：00：00
     * */
    public static Date getStartTime(Date current) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(current);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }
    /**
     *  获取日期的结束时间   2019-07-09 11：11：11  -> 2019-07-09 23：59：59
     * */
    public static Date getEndTime(Date current) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(current);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }
    /**
     * 获取距离今天结束的时间
     * @return 获取距离今天结束的时间（毫秒）
     * */
    public static long getTodayEndMilSeconds(Date date){
        Date now = new Date();
        Date end = getEndTime(now);
        long time = end.getTime() - date.getTime();
        return time;
    }

}
