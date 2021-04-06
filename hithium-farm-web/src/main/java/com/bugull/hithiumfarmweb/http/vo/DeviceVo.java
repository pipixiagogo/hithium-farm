package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DeviceVo {

    private String deviceName;

    private String name;

    private String description;

    private List<Map<String, Map<String,Map<String,Map<String,List<String>>>>>> deviceMap;
}
