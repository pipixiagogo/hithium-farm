package com.bugull.hithium.core.entity;

import lombok.Data;

@Data
public class Equipment  {
    private String id;

    private Boolean isHasData=false;
    /**
     * 数据类型
     */
    private String dataType;

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

    private String equipmentTypeInfo;

    private String deviceName;
}
