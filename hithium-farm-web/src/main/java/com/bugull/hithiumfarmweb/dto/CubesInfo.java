package com.bugull.hithiumfarmweb.dto;

import lombok.Data;

@Data
public class CubesInfo {
    private Integer pccId;
    private Integer clientId;
    private Integer batteryType;
    private Integer batteryLevel;
    private String batteryChargeDischargeRate;
    private Integer cubeModel;
    private String cubeCapacity;
    private String cubePower;
    private String batteryManufacturer;
    private Integer cellCapacity;
    private Integer coolingMode;
    private String description;
    private String name;
    /**
     * 无用 ProjectId、StationUniqueId、IsCloud
     */
    private Integer projectId;
    private Integer stationUniqueId;
    private boolean isCloud;
    private Integer id;
}
