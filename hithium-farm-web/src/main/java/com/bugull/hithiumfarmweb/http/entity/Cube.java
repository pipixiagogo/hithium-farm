package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.List;

/**
 * 储能箱
 */
@Data
@Entity
public class Cube extends SimpleEntity {
    private Integer stationId;
    private Integer pccId;
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
    private Integer cubeId;

    private String deviceName;



    private List<String> equipmentIds;
}
