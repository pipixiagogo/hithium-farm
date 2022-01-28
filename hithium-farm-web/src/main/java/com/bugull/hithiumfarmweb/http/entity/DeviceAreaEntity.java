package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

@Data
public class DeviceAreaEntity  {

    //设备唯一名称
    private String deviceName;
    private String province;
    private String city;
    private String longitude;
    private String latitude;
    /**
     * 站内描述
     */
    private String description;
    /**站内名称
     */
    private String name;
}
