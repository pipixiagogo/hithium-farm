package com.bugull.hithium.core.entity;

import lombok.Data;

import java.util.List;

/**
 * 储能箱
 */
@Data
public class Cube {
    private Integer stationId;
    private Integer pccId;
    private Integer batteryType;
    private Integer batteryLevel;
    private String batteryChargeDischargeRate;
    private Integer cubeModel;
    private String cubeCapacity;
    private String cubePower;
    private String batteryManufacturer;
    private Integer cellCapacity=0;
    private Integer coolingMode;
    private String description;
    private String name;
    private Integer cubeId;

    private String deviceName;



    private List<String> equipmentIds;
}
