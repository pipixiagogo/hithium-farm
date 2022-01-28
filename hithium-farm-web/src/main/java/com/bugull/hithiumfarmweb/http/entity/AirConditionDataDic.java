package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.*;
import lombok.Data;

import java.util.Date;

/**
 * 空调
 */
@Data
public class AirConditionDataDic {
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
     * ColdTempSetpoint	int	制冷设定点	"制冷开启=制冷设定点+制冷偏差
     * 制冷停止=制冷设定点"
     * ColdTempOffset	int	制冷偏差
     * HighTempAlarm	int	高温告警值
     * LowTempAlarm	int	低温告警值
     * HeatTempSetpoint	int	加热设定点	"制热停止=加热设定点+加热偏差
     * 制热开启=加热设定点"
     * HeatTempOffset	int	加热偏差
     * CurrTemperature	int	柜内当前温度
     * CurrHumidity	int	柜内当前湿度
     */
    @ExcelProperty(value = "设备名称")
    private String name;//设备名称
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @ExcelIgnore
    private String time;//时间
    @ExcelIgnore
    private Integer equipmentId;//设备ID

    @ExcelProperty(value = "前置机通道状态",converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常 默认值异常

    @ExcelProperty(value = "制冷开启")
    private Integer coldTempSetpoint; //制冷开启=制冷设定点+制冷偏差  制冷停止= 制冷设定点

    @ExcelProperty(value = "制冷偏差")
    private Integer coldTempOffset;//制冷偏差

    @ExcelProperty(value = "高温告警值")
    private Integer highTempAlarm;//高温告警值

    @ExcelProperty(value = "低温告警值")
    private Integer lowTempAlarm;//低温告警值

    @ExcelProperty(value = "加热设定点")
    private Integer heatTempSetpoint;//加热设定点  制热停止=加热设定点+加热偏差  制热开启=加热设定点

    @ExcelProperty(value = "加热偏差")
    private Integer heatTempOffset;//加热偏差

    @ExcelProperty(value = "当前柜内温度")
    private Integer currTemperature;//当前柜内温度

    @ExcelProperty(value = "当前柜内湿度")
    private Integer currHumidity;//当前柜内湿度

    @ExcelProperty(value = "传感器故障",converter = AirSensorFaultConverter.class)
    private Integer sensorFault;//传感器故障	1= 故障

    @ExcelProperty(value = "高低电压告警",converter = AirAlarmConverter.class)
    private Integer highLowVoltAlarm;//高低电压告警	1= 告警

    @ExcelProperty(value = "高低温告警",converter = AirAlarmConverter.class)
    private Integer highLowTempAlarm;//高低温告警  1= 告警

    @ExcelProperty(value = "高低压告警",converter = AirAlarmConverter.class)
    private Integer highLowPressAlarm;//高低压告警 1= 告警
    @ExcelProperty(value = "压缩机告警",converter = AirAlarmConverter.class)
    private Integer compressorAlarm;//压缩机告警 1= 告警
    @ExcelProperty(value = "开关机",converter = AirPowerOnOffConverter.class)
    private Integer powerOnOff;//开关机  1=开机
    @ExcelProperty(value = "制冷",converter = AirSwitchConverter.class)
    private Integer cold;//制冷 1 开启
    @ExcelProperty(value = "送风",converter = AirSwitchConverter.class)
    private Integer air;//送风 1 开启
    @ExcelProperty(value = "待机",converter = AirSwitchConverter.class)
    private Integer standby;//待机 1=开启
    @ExcelProperty(value = "制热",converter = AirSwitchConverter.class)
    private Integer heat;//制热 1=开启
    @ExcelProperty(value = "除湿开启温度")
    private Integer arefactionOnTemp;//除湿开启温度
    @ExcelProperty(value = "除湿停止温度")
    private Integer arefactionOffTemp;//除湿停止温度
    @ExcelIgnore
    private Integer ArefactionOnhumidity;//除湿开启湿度
    @ExcelProperty(value = "除湿开启湿度")
    private Integer arefactionOnhumidityOfExcel;
    @ExcelIgnore
    private Integer ArefactionOffhumidity;//除湿停止湿度
    @ExcelProperty(value = "除湿停止湿度")
    private Integer arefactionOffhumidityOfExcel;

    public Integer getArefactionOnhumidityOfExcel() {
        return ArefactionOnhumidity;
    }

    public Integer getArefactionOffhumidityOfExcel() {
        return ArefactionOffhumidity;
    }
    /*** SensorFault	int	传感器故障	1= 故障
     * HighLowVoltAlarm	int	高低电压告警	1= 告警
     * HighLowTempAlarm	int	高低温告警	1= 告警
     * HighLowPressAlarm	int	高低压告警	1= 告警
     * CompressorAlarm	int	压缩机告警	1= 告警
     * PowerOnOff	int	开关机	1= 开机
     * Cold	int	 制冷	1= 开启
     * Air	int	 送风	1= 开启
     * Standby	int	 待机	1= 开启
     * Heat	int	 制热	1= 开启
     * ArefactionOnTemp	int	 除湿开启温度	默认值 -9999
     * ArefactionOffTemp	int	 除湿停止温度	默认值 -9999
     * ArefactionOnhumidity	int	 除湿开启湿度	默认值 -9999
     * ArefactionOffhumidity	int	 除湿停止湿度	默认值 -9999
     */
}
