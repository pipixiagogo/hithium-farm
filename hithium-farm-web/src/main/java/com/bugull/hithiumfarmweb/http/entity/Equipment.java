package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Equipment  {

    private String id;
    private Integer pccId;
    private Integer cubeId;
    private Integer stationId;
    private Integer cabinId;
    private boolean enabled;

    /**
     * 厂家
     */
    private String manufacturer;
    /**
     * 型号
     */
    private String model;
    private String description;
    private String name;
    private Integer equipmentId;

    private String deviceName;

    private String equipmentTypeInfo;

}
