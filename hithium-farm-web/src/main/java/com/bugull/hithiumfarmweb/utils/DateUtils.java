

package com.bugull.hithiumfarmweb.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

/**
 * 日期处理
 *
 */
public class DateUtils {
    /**
     * 一天的秒数
     */
    public final static Integer DAY_OF_SECONDS = 60 * 60 * 24;
    public final static Integer HOUR_OF_SECONDS = 60 * 60;
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    public final static String DATE_PATTERN_MM_DD = "MM-dd";
    public final static String DATE_PATTERN_HH = "yyyy-MM-dd HH";
    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public final static String DATE_TIME_NAME_IMG_PATTERN="yyyy-MM-dd-HH-mm-ss";

    public final static String DATE_TIME_PATTERN_MM = "yyyy-MM-??";

    public final static String DATE_TIME_PATTERN_YY = "yyyy-??-??";

    public final static String DATE_TIME_PATTERN_HH = "HH:mm";

    /** 时间格式(yyyy-MM) */
    public final static String DATE_PATTERN_YYYY_MM = "yyyy-MM";
    /** 时间格式(yyyy) */
    public final static String DATE_PATTERN_YYYY = "yyyy";
    private static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfYYYY = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdf_MM_DD = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN_MM_DD);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdf_HH = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN_HH);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfDate = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_TIME_PATTERN);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfDateWithImg = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_TIME_NAME_IMG_PATTERN);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfWithYYYYMMHH = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN);
        }
    };

    private static ThreadLocal<SimpleDateFormat> sdfOfYYYYWW = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN_YYYY_MM);
        }
    };

    private static ThreadLocal<SimpleDateFormat> sdfWithYYYYMM = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_TIME_PATTERN_MM);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfWithYYYY = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_TIME_PATTERN_YY);
        }
    };
    private static ThreadLocal<SimpleDateFormat> sdfWithHHmm = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_TIME_PATTERN_HH);
        }
    };

    public static Date dateToStrWithHHmm(Date dateDate) throws ParseException {
        SimpleDateFormat formatter = sdfWithHHmm.get();
        return formatter.parse(formatter.format(dateDate));
    }

    public static Date dateToStrWithHHmmWith(String dateDateStr) throws ParseException {
        SimpleDateFormat formatter = sdfWithHHmm.get();
        return formatter.parse(dateDateStr);
    }

    public static Date dateToStrWithHHmm(String dateDateStr) throws ParseException {
        SimpleDateFormat formatter = sdfWithYYYYMMHH.get();
        return formatter.parse(dateDateStr);
    }

    public static Date strToDate(String dateDateStr) throws ParseException {
        SimpleDateFormat formatter = sdfDate.get();
        return formatter.parse(dateDateStr);
    }
    public static Date strToDateYYYY(String dateDateStr) throws ParseException {
        SimpleDateFormat formatter = sdfYYYY.get();
        return formatter.parse(dateDateStr);
    }


    public static String strToDateWithDATE_TIME_PATTERN(Date date) throws ParseException {
        SimpleDateFormat formatter = sdfDateWithImg.get();
        return formatter.format(date);
    }

    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = sdf.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStrMMDD(Date dateDate) {
        SimpleDateFormat formatter = sdf_MM_DD.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStrWithHH(Date dateDate) {
        SimpleDateFormat formatter = sdf_HH.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStryyyymm(Date dateDate) {
        SimpleDateFormat formatter = sdfWithYYYYMM.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM
     * @param date  日期
     * @return 返回yyyy-MM格式日期
     */
    public static String formatYYYYMM(Date date) {
        return format(date, DATE_PATTERN_YYYY_MM);
    }

    /**
     * 日期格式化 日期格式为：yyyy
     * @param date  日期
     * @return 返回yyyy-MM格式日期
     */
    public static String formatYYYY(Date date) {
        return format(date, DATE_PATTERN_YYYY);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    public static String dateToStryyyy(Date dateDate) {
        SimpleDateFormat formatter = sdfWithYYYY.get();
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String timeDifferent(Date start, Date end) {
        return String.valueOf((start.getTime() - end.getTime()) / 60000);
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

    public static Date getCurrentYearStartTime() {
        Calendar c = Calendar.getInstance();
        c.set(MONTH, 0);
        c.set(DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        c.set(MONTH, 11);
        c.set(DATE, 31);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    public static Date getPreviousYearStartTime(Date current){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.YEAR, -1);
        c.add(MONTH,1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    public static Date addDateMinutes(Date current,int minute){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.MINUTE,minute);
        return c.getTime();
    }
    public static Date addDateDays(Date current,int date){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(DATE,date);
        return c.getTime();
    }

    public static Date addDateMonths(Date current,int month){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(MONTH,month);
        return c.getTime();
    }

    public static Date addDateYears(Date current,int year){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.YEAR,year);
        return c.getTime();
    }






}
