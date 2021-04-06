package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 空调
 */
@Data
@Entity
public class AirConditionDataDic extends SimpleEntity {

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
    private String name;//设备名称
    private Date generationDataTime;
    @Ignore
    private String time;//时间
    private Integer equipmentId;//设备ID
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常 默认值异常
    private Integer coldTempSetpoint; //制冷开启=制冷设定点+制冷偏差  制冷停止= 制冷设定点

    private Integer coldTempOffset;//制冷偏差
    private Integer highTempAlarm;//高温告警值
    private Integer lowTempAlarm;//低温告警值

    private Integer heatTempSetpoint;//加热设定点  制热停止=加热设定点+加热偏差  制热开启=加热设定点

    private Integer heatTempOffset;//加热偏差
    private Integer currTemperature;//当前柜内温度
    private Integer currHumidity;//当前柜内湿度

    private Integer sensorFault;//传感器故障	1= 故障
    private Integer highLowVoltAlarm;//高低电压告警	1= 告警
    private Integer highLowTempAlarm;//高低温告警  1= 告警
    private Integer highLowPressAlarm;//高低压告警 1= 告警
    private Integer compressorAlarm;//压缩机告警 1= 告警
    private Integer powerOnOff;//开关机  1=开机
    private Integer cold;//制冷 1 开启
    private Integer air;//送风 1 开启
    private Integer standby;//待机 1=开启
    private Integer heat;//制热 1=开启
    private Integer arefactionOnTemp;//除湿开启温度
    private Integer arefactionOffTemp;//除湿停止温度
    private Integer ArefactionOnhumidity;//除湿开启湿度
    private Integer ArefactionOffhumidity;//除湿停止湿度
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
