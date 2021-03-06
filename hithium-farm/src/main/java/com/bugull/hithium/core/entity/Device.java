package com.bugull.hithium.core.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 储能站
 */
@Data
public class Device  {

    //设备唯一名称
    private String deviceName;

    private String stationCapacity;

    private String stationPower;
    private String province;
    private String city;
    private String longitude;
    private String latitude;
    /**
     * TODO 根据经纬度查询出详细地址
     */
    private String address;
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
    /**
     * 站内名称
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
//    @EmbedList
    private List<TimeOfPriceBo> priceOfTime;
//    @EmbedList
    private List<TimeOfPowerBo>timeOfPower;

    //收益
    private String income = "0";
    //总充电量
    private String dischargeCapacitySum;
    //总充电量
    private String chargeCapacitySum;

    private Boolean bindStation=false;


}
