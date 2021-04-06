package com.bugull.hithiumfarmweb.enumerate;


import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum DataType {
    DEVICE_LIST_DATA("DEVICE_LIST_DATA"),
    BREAKDOWNLOG_DATA("BREAKDOWNLOG_DATA"),
    REAL_TIME_DATA("REAL_TIME_DATA"),
    UNKNOW();
    private static Map<String,DataType> map;
    DataType(String ... msg){
        init(msg);
    }

    private void init(String[] msgs) {
        if(map == null){
            map = new HashMap();
        }
        for(String msg:msgs){
            if(!StringUtils.isEmpty(msg)){
                map.put(msg,this);
            }
        }
    }

    public static DataType value(String msg){
        DataType dataType = map.get(msg);
        if(dataType == null)
            return DataType.UNKNOW;
        return dataType;
    }
}
