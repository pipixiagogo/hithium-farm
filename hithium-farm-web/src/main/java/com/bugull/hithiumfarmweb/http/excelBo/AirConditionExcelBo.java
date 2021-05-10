package com.bugull.hithiumfarmweb.http.excelBo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AirConditionExcelBo {
    @ExcelProperty(value = "设备名称")
    private String name;//设备名称
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
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
    @ExcelProperty(value = "传感器故障")
    private String sensorFaultMsg;//传感器故障	1= 故障
    @ExcelProperty(value = "高低电压告警")
    private Integer highLowVoltAlarm;//高低电压告警	1= 告警
    @ExcelProperty(value = "高低温告警")
    private Integer highLowTempAlarm;//高低温告警  1= 告警
    @ExcelProperty(value = "高低压告警")
    private Integer highLowPressAlarm;//高低压告警 1= 告警
    @ExcelProperty(value = "压缩机告警")
    private Integer compressorAlarm;//压缩机告警 1= 告警
    @ExcelProperty(value = "开关机")
    private Integer powerOnOff;//开关机  1=开机
    @ExcelProperty(value = "制冷")
    private Integer cold;//制冷 1 开启
    @ExcelProperty(value = "送风")
    private Integer air;//送风 1 开启
    @ExcelProperty(value = "待机")
    private Integer standby;//待机 1=开启
    @ExcelProperty(value = "制热")
    private Integer heat;//制热 1=开启
    @ExcelProperty(value = "除湿开启温度")
    private Integer arefactionOnTemp;//除湿开启温度
    @ExcelProperty(value = "除湿停止温度")
    private Integer arefactionOffTemp;//除湿停止温度
    @ExcelProperty(value = "除湿开启湿度")
    private Integer arefactionOnhumidity;//除湿开启湿度
    @ExcelProperty(value = "除湿停止湿度")
    private Integer arefactionOffhumidity;//除湿停止湿度
}
