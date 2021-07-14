package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.hithiumfarmweb.http.bo.TimeOfPowerBo;
import com.bugull.hithiumfarmweb.http.bo.TimeOfPriceBo;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.RefList;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 储能站
 */
@Entity
@Data
@EnsureIndex("{deviceName:1}")
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

    @EmbedList
    private List<TimeOfPriceBo> priceOfTime;

    @EmbedList
    private List<TimeOfPowerBo> timeOfPower;
    //收益
    private String income = "0";

    //总充电量
    private String dischargeCapacitySum = "0";
    //总充电量
    private String chargeCapacitySum = "0";

    //是否已经绑定电站
    private Boolean bindStation=false;

}
