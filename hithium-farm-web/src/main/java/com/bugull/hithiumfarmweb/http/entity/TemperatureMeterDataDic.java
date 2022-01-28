package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.EquipStatusConverter;
import lombok.Data;

import java.util.Date;

/**
 * 温湿度
 */
@Data
public class TemperatureMeterDataDic  {
    @ExcelIgnore
    private String id;
    @ExcelIgnore
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
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @ExcelProperty(value = "设备名称")
    private String name;//设备名称
    @ExcelIgnore
    private String time;
    @ExcelIgnore
    private Integer equipmentId;
    @ExcelProperty(value = "前置机通道状态", converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常 默认值异常
    @ExcelProperty(value = "温度 单位:℃")
    private String temperature;//温度
    @ExcelProperty(value = "湿度")
    private String humidity;//湿度
}
