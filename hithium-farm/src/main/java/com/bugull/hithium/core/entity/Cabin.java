package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.List;


/**
 * 舱信息
 */
@Entity
@Data
public class Cabin extends SimpleEntity {
    private Integer stationId;
    private Integer pccId;
    private Integer cabinType;
    private Integer cubeId;

    private String name;

    private Integer cabinId;

    private String deviceName;

    private List<String> equipmentIds;
}
