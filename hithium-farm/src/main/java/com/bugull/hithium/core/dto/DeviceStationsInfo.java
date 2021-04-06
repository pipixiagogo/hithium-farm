package com.bugull.hithium.core.dto;


import lombok.Data;

/**
 * 接受设备储能站信息
 */
@Data
public class DeviceStationsInfo {
    private String stationCapacity;
    private String stationPower;
    private String province;
    private String city;
    private String longitude;
    private String latitude;
    private Integer applicationScenarios;
    private Integer applicationScenariosItem;
    private String description;
    private String name;
    /**
     * 无用 projectId、stationUniqueId、isCloud
     */
    private Integer projectId;
    private Integer stationUniqueId;
    private boolean isCloud;
    //站编号
    private Integer id;
}
