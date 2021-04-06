package com.bugull.hithium.core.dto;

import lombok.Data;

/**
 * 储能设备信息
 */
@Data
public class EquipmentInfo {

    private Integer pccId;
    private Integer cubeId;
    private Integer stationId;
    private Integer cabinId;
    private boolean enabled;

    private Integer equipmentType;

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
    private Integer id;


}
