package com.bugull.hithium.core.entity;

import lombok.Data;

import java.util.Date;

/**
 * 温湿度
 */
@Data
public class TemperatureMeterDataDic {
    private String deviceName;
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常
     * "
     * Temperature	String	温度	默认值-9999f
     * Humidity	String	湿度	默认值-9999f
     */
    private Date generationDataTime;
    private String name;//设备名称
    private String time;
    private Integer equipmentId;
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常 默认值异常
    private String temperature;//温度
    private String humidity;//湿度
}
