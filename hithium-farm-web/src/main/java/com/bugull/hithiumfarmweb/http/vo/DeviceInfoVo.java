package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DeviceInfoVo {
    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "设备描述")
    private String description;

    @ApiModelProperty(value = "设备所处位置")
    private String areaCity;

    @ApiModelProperty(value = "设备接入时间")
    private Date accessTime;

    /**
     * 30分钟内没数据 上报就认为停机
     */
    @ApiModelProperty(value = "设备状态 停机:正在运行")
    private String deviceStatus;

    private String deviceName;

    @ApiModelProperty(value = "储能站容量")
    private String stationCapacity;
    @ApiModelProperty(value = "储能站功率")
    private String stationPower;

    /**
     * 应用场景
     */
    @ApiModelProperty(value = "应用场景")
    private String applicationScenariosMsg;
    /**
     * 应用场景的策略
     */
    @ApiModelProperty(value = "应用场景下的策略")
    private String applicationScenariosItemMsg;

    //收益
    @ApiModelProperty(value = "收益")
    private String income = "0";

    //总充电量
    @ApiModelProperty(value = "总放电量")
    private String dischargeCapacitySum;
    //总充电量
    @ApiModelProperty(value = "总充电量")
    private String chargeCapacitySum;

    @ApiModelProperty(value = "Pcs运行状态数据")
    private List<PcsStatusVo> pcsStatusVoList;
}
