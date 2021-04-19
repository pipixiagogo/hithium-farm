package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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

}
