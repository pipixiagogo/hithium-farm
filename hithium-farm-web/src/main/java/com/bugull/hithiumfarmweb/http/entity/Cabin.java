package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.List;


/**
 * 舱信息
 */
@Data
public class Cabin  {
    private Integer stationId;
    private Integer pccId;
    private Integer cabinType;
    private Integer cubeId;

    private String name;

    private Integer cabinId;

    private String deviceName;

    private List<String> equipmentIds;
}
