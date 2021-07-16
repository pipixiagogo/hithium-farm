package com.bugull.hithiumfarmweb.common;

import com.bugull.hithiumfarmweb.annotation.SortField;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 常量类
 */
public class Const {

    public static final String REGEX_MOBILE = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
    public static final String NO_QUERY_PERMISSION="该账户无查询该设备权限";
    public static final String NO_MODIFY_PERMISSION="该账户无修改该设备权限";
    public static final String RUNNING_STATUS_MSG_START="开机";
    public static final String RUNNING_STATUS_MSG_DOWN="关机";
    public static final String SEPARATOR_OF_MAP="/";
    public static final String PASSWORD_PATTERN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,8}$";
    public static final String DEVICE_NAME="deviceName";
    public static final String EQUIPMENT_ID="equipmentId";
    public static final String STATUS="status";
    public static final String GENERATION_DATA_TIME="generationDataTime";
    public static final String PROVINCE="province";
    /**
     * 时间格式正则表达式yyyy-MM-dd
     */
     public final static Pattern DATE_PATTERN = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\-\\s]?((((0?" +"[13578])|(1[02]))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))" +"|(((0?[469])|(11))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|" +"(0?2[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12" +"35679])|([13579][01345789]))[\\-\\-\\s]?((((0?[13578])|(1[02]))" +"[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))" +"[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\-\\s]?((0?[" +"1-9])|(1[0-9])|(2[0-8]))))))");

    public static final String BAMS_DATA_SHEET_NAME="电池堆";
    public static final String PCS_DATA_SHEET_NAME="PCS主控";
    public static final String BREAKDOWNLOG_DATA_SHEET_NAME="告警日志";
    public static final String INCOMEOFDAY="{_id:'$incomeOfDay',count:{$sum:{$toDouble:'$income'}}}";
//    {_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } }
//    "{_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } },count:{$sum:{$toDouble:'$stationPower'}}}"
    public static final String INCOMEOFMONTH="{_id:{$substrCP:['$incomeOfDay',0,7]},count:{$sum:{$toDouble:'$income'}}}";
    public static final String INCOMEOFYEAR="{_id:{$substrCP:['$incomeOfDay',0,4]},count:{$sum:{$toDouble:'$income'}}}";

    public static final String DISCHARGECAPACITY_HOUR="{_id:{$dateToString:{format:'%Y-%m-%d %H',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}";
    public static final String DISCHARGECAPACITY_DAY="{_id:{$dateToString:{format:'%Y %m-%d',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}";
    public static final String DISCHARGECAPACITY_MONTH="{_id:{$dateToString:{format:'%Y-%m',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}";

    public static final String START_TIME="00:00-";
    public static final String END_TIME="-24:00";

    public static final String DAY="DAY";
    public static final String YEAR="YEAR";

    public static final String CHECK_TIME_FORMAT="(0\\d{1}|1\\d{1}|2[0-3]):([0-5]\\d{1})";
    public static final String COUNTRY="china";
    /**
     * 充放电电量
     */
    public static final String DISCHARGECAPACITYSUM="DISCHARGE-CAPACITY-SUM-";
    public static final String CHARGECAPACITYSUM="CHARGE-CAPACITY-SUM-";

    public static final String MONTH ="MONTH";
    public static final String QUARTER ="QUARTER";
    public static final int MAX_YEAR = 2200;
    public static final int MIN_YEAR = 2000;
    public static final String RESET_PWD="aa123456";
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
    public static final String AMMETER_DATADIC_TABLE="AMMETER_DATADIC_TABLE";
    public static final String BAMS_DATADICDA_TABLE="BAMS_DATADICDA_TABLE";
//    bmsCellTempDataDicDao
    public static final String BMSCELL_TEMP_DATADIC_TABLE="BMSCELL_TEMP_DATADIC_TABLE";
//    bmsCellVoltDataDicDao
    public static final String BMSCELL_VOL_DATADIC_TABLE="BMSCELL_VOL_DATADIC_TABLE";
//    bcuDataDicBCUDao
    public static final String BCU_DATADIC_TABLE="BCU_DATADIC_TABLE";
//    pcsCabinetDicDao
    public static final String PCS_CABINETDIC_TABLE="PCS_CABINETDIC_TABLE";
//    pcsChannelDicDao
    public static final String PCS_CHANNELDIC_TABLE="PCS_CHANNELDIC_TABLE";
//    airConditionDataDicDao
    public static final String AIRCONDITION_DATADIC_TABLE="AIRCONDITION_DATADIC";
//    temperatureMeterDataDicDao
    public static final String TEMPERATURE_METERDATADIC_TABLE="TEMPERATURE_METERDATADIC_TABLE";
//    fireControlDataDicDao
    public static final String FIRECONTROL_DATADIC_TABLE="FIRECONTROL_DATADIC_TABLE";
//    upsPowerDataDicDao
    public static final String UPSPOWER_DATADIC_TABLE="UPSPOWER_DATADIC_TABLE";

    public static final Map<String, Set<String>> SORT_FIELDS = new HashMap<>();

    static {
        Set<String> breakdownlogFields = new HashSet<>();
        getField(breakdownlogFields, "", BreakDownLog.class);
        SORT_FIELDS.put(BREAKDOWNLOG_TABLE, breakdownlogFields);
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
