package com.bugull.hithiumfarmweb.common;

import com.bugull.hithiumfarmweb.annotation.SortField;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 常量类
 */
public class Const {

    public static ConcurrentHashMap<String,Object> CACHEMAP=new ConcurrentHashMap<>();
    /**
     * 科创实时数据类型
     */
    //排序
    public static final Integer DESC = -1;
    public static final Integer ASC = 1;

    public static final String WEBTHREAD_POOL_TASK = "web-thread-pool-task";

    public static final String PAGE = "page";
    public static final String PAGESIZE = "pageSize";

    public static final String PROJECT_TYPE_ESS="$ESS-";

    public static final String SQL_PREFIX = "$ESS/";
    public static final String D2S_SUFFIX = "/MSG/D2S";
    public static final String S2D_SUFFIX = "/MSG/S2D";

    public static final String PARAM_ILL = "参数错误";
    public static final String BREAKDOWNLOG_TABLE = "BREAKDOWNLOG_TABLE";
    public static final Map<String, Set<String>> SORT_FIELDS = new HashMap<>();

    static {

        Set<String> deviceFields = new HashSet<>();
        getField(deviceFields, "", BreakDownLog.class);
        SORT_FIELDS.put(BREAKDOWNLOG_TABLE, deviceFields);
    }

    /**
     * 订阅
     * @param connetName
     * @return
     */
    public static String getSubClientId(String connetName){
        return connetName+"-1";
    }

    /**
     * 发布
     * @param connetName
     * @return
     */
    public static String getpubClientId(String connetName){
        return connetName+"-2";
    }


    public static String getS2DTopic(String projectType, String connetName) {
        return "$" + projectType + "/" + connetName + S2D_SUFFIX;
    }

    public static String getD2STopic(String projectType, String connetName) {
        return "$" + projectType + "/" + connetName + D2S_SUFFIX;
    }

    private static void getField(Set<String> deviceFields, String preFix, Class<?> deviceClass) {
        Field[] fields = deviceClass.getDeclaredFields();
        for (Field f : fields) {
            SortField sf = f.getAnnotation(SortField.class);
            if (sf != null) {
                String name = f.getName();
                Class clazz = f.getType();
                if (clazz.isArray()) {
                    continue;
                }
                if (clazz.isPrimitive()) {
                    deviceFields.add(preFix + name);
                    continue;
                }
                if (Number.class.isAssignableFrom(clazz) ||
                        Date.class.isAssignableFrom(clazz) ||
                        String.class.isAssignableFrom(clazz)) {
                    deviceFields.add(preFix + name);
                    continue;
                }
                getField(deviceFields, preFix + name + ".", clazz);
            }
        }
        return;
    }
}
