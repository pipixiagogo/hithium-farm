package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DeviceInfoVo {
    private String name;
    private String description;

    private String areaCity;

    private Date accessTime;

    /**
     * 30分钟内没数据 上报就认为停机
     */
    private String deviceStatus;

    private String deviceName;

    private String stationCapacity;
    private String stationPower;

    /**
     * 应用场景
     */
    private String applicationScenariosMsg;
    /**
     * 应用场景的策略
     */
    private String applicationScenariosItemMsg;


    private String income = "0";
    private String dayDeviceIncome="0";

    //总充电量
    private String dischargeCapacitySum;
    //总充电量
    private String chargeCapacitySum;

    private List<PcsStatusVo> pcsStatusVoList;

    private List<String> deviceImgIdWithUrls;

}
