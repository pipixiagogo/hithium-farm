package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.EquipStatusConverter;
import lombok.Data;

import java.util.Date;

/**
 * 后备电源
 */
@Data
public class UpsPowerDataDic  {

    @ExcelIgnore
    private String id;
    @ExcelIgnore
    private String deviceName;
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常
     * PhaseRvoltage	String	R相市电电压	精度0.1V
     * PhaseSvoltage	String	S相市电电压	精度0.1V
     * PhaseTvoltage	String	T相市电电压	精度0.1V
     * Frequency	String	市电频率	精度0.1V
     * OutVoltage	String	输出电压	精度0.1V
     * ShortVoltageH	String	短路电压	精度0.1V
     * OutFrequency	String	输出频率	精度0.1V
     * OutCurrent	String	输出电流	精度0.1V
     * BattVoltage	String	电池电压	精度0.1V
     * ShortVoltageV	String	短路电压	精度0.1V
     */
    @ExcelProperty(value = "设备名称")
    private String name;//设备名称
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @ExcelIgnore
    private String time;
    @ExcelIgnore
    private Integer equipmentId;//设备ID
    @ExcelProperty(value = "前置机通道状态",converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常
    @ExcelProperty(value = "R相市电电压")
    private String phaseRvoltage;//R相市电电压  精度0.1V
    @ExcelProperty(value = "S相市电电压")
    private String phaseSvoltage;//S相市电电压  精度0.1V
    @ExcelProperty(value = "T相市电电压")
    private String phaseTvoltage;//T相市电电压  精度0.1V
    @ExcelProperty(value = "市电频率")
    private String frequency;//市电频率 精度0.1V
    @ExcelProperty(value = "输出电压")
    private String outVoltage;//输出电压 精度0.1V
    @ExcelProperty(value = "短路电压")
    private String shortVoltageH;//短路电压 精度0.1V
    @ExcelProperty(value = "输出频率")
    private String outFrequency;//输出频率
    @ExcelProperty(value = "输出电流")
    private String outCurrent;//输出电流
    @ExcelProperty(value = "电池电压")
    private String battVoltage;//电池电压
    @ExcelProperty(value = "短路电压")
    private String shortVoltageV;//短路电压

    /**
     * Temperature	String	温度	精度0.1℃
     * BattRemainTime	String	电池剩余备用时间估计
     * BattSoc	String	电池容量百分比
     * CurrRunMode	short	当前运行模式
     * CurrBattTestStatus	short	当前电池测试状态
     * FaultCode	short	故障代码
     * WarningCode	short	警告代码
     * ErrorCode	short	错误代码
     * ErrPowerVoltage	short	错误的市电电压
     * ErrPowerFrequency	String	错误的市电频率
     * ErrInvVoltage	short	错误的逆变电压
     * ErrInvFrequency	String	错误的逆变频率
     * ErrLoader	short	错误的负载
     */
    @ExcelProperty(value = "温度")
    private String temperature;//温度  精度0.1℃
    @ExcelProperty(value = "电池剩余备用时间估计")
    private String battRemainTime;//电池剩余备用时间估计
    @ExcelProperty(value = "电池容量百分比")
    private String battSoc;//电池容量百分比
    @ExcelProperty(value = "当前运行模式")
    private Short currRunMode;//当前运行模式
    @ExcelProperty(value = "当前电池测试状态")
    private Short currBattTestStatus;//当前电池测试状态
    @ExcelProperty(value = "故障代码")
    private Short faultCode;//故障代码
    @ExcelProperty(value = "警告代码")
    private Short warningCode;//警告代码
    @ExcelProperty(value = "错误代码")
    private Short errorCode;//错误代码
    @ExcelProperty(value = "错误的市电电压")
    private Short errPowerVoltage;//错误的市电电压
    @ExcelProperty(value = "错误的市电频率")
    private String errPowerFrequency;//错误的市电频率
    @ExcelProperty(value = "错误的逆变电压")
    private Short errInvVoltage;//错误的逆变电压
    @ExcelProperty(value = "错误的逆变频率")
    private String errInvFrequency;//错误的逆变频率
    @ExcelProperty(value = "错误的负载")
    private Short errLoader;//错误的负载
    @ExcelProperty(value = "错误的输出电流百分比")
    private Short errOutCurrentP;//错误的输出电流百分比
    @ExcelProperty(value = "错误的PBUS电压")
    private Short errPbusVoltage;//错误的PBUS电压
    @ExcelProperty(value = "错误的NBUS电压")
    private Short errNbusVoltage;//错误的NBUS电压
    @ExcelProperty(value = "错误的电池电压")
    private String errBattVoltage;//错误的电池电压
    @ExcelProperty(value = "错误的温度")
    private String errTemperature;//错误的温度
    @ExcelProperty(value = "错误的PFC状态")
    private Short errPfcStatus;//错误的PFC状态
    @ExcelProperty(value = "错误的逆变状态")
    private Short errInvStatus;//错误的逆变状态
    @ExcelProperty(value = "错误的整流状态")
    private Short errRectStatus;//错误的整流状态
    @ExcelProperty(value = "错误的逆变继电器状态")
    private Short errInvRelayStatus;//错误的逆变继电器状态
    /** ErrOutCurrentP	short	错误的输出电流百分比
     * ErrPbusVoltage	short	错误的PBUS电压
     * ErrNbusVoltage	short	错误的NBUS电压
     * ErrBattVoltage	String	错误的电池电压
     * ErrTemperature	String	错误的温度
     * ErrPfcStatus	short	错误的PFC状态
     * ErrInvStatus	short	错误的逆变状态
     * ErrRectStatus	short	错误的整流状态
     * ErrInvRelayStatus	short	错误的逆变继电器状态
     */
}
