package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class BmsCellDataBo {

    private Date generationDataTime;

    private String name;

    private Map<String, Integer> tempMap;

    private Map<String,Integer> volMap;

    private String deviceName;

    private Integer equipmentId;
}
