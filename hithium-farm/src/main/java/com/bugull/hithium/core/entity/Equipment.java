package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@EnsureIndex("{deviceName:1}")
public class Equipment extends SimpleEntity {

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

    private String deviceName;
}
