/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.bugull.hithiumfarmweb.utils;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    /**
     * 字符串转换成日期
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 根据周数，获取开始日期、结束日期
     * @param week  周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date 日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date 日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date 日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

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
     * 对日期的【周】进行加/减
     *
     * @param date 日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date 日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date 日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
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
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }


}
