package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.http.bo.EquipmentBo;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DeviceVo {

    private String deviceName;

    private String name;

    private String description;

//    private List<Map<String, Map<String, Map<String, Map<String, List<String>>>>>> deviceMap;
    private EquipmentBo equipmentBo;
}
