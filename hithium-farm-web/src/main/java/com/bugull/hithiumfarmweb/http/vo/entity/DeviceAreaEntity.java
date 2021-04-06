package com.bugull.hithiumfarmweb.http.vo.entity;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

@Entity(name = "device")
@Data
public class DeviceAreaEntity extends SimpleEntity {

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
