package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 储能站
 */
@Entity
@Data
public class Device extends SimpleEntity {

    //设备唯一名称
    private String deviceName;

    private String stationCapacity;

    private String stationPower;
    private String province;
    private String city;
    private String longitude;
    private String latitude;
    /**
     * 应用场景
     */
    private Integer applicationScenarios;
    /**
     * 应用场景的策略
     */
    private Integer applicationScenariosItem;
    /**
     * 站内描述
     */
    private String description;
    /**站内名称
     */
    private String name;
    /**
     * 站内编号
     */
    private Integer stationId;
    /**
     * 并网口ID
     */
    private List<String> pccsIds;

    private List<String> equipmentIds;

    private Date accessTime;


}
