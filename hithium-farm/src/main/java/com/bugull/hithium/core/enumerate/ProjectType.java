package com.bugull.hithium.core.enumerate;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum ProjectType {
    KE_ESS("KC_ESS"),
    UNKNOW();


    private static  Map<String,ProjectType> map;
    ProjectType(String ... msg){
       init(msg);

    }

    private void init(String... msgs) {
        if(map==null){
            map = new HashMap<>();
        }
        for(String msg:msgs){
            if(!StringUtils.isEmpty(msg)){
                map.put(msg,this);
            }
        }

    }

    public static ProjectType value(String msg){
        ProjectType type = map.get(msg);
        if(type==null)
            return ProjectType.UNKNOW;
        return type;
    }
}
