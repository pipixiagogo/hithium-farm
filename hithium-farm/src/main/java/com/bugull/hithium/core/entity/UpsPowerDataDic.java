package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.awt.image.ShortLookupTable;
import java.util.Date;

/**
 * 后备电源
 */
@Data
@Entity
public class UpsPowerDataDic extends SimpleEntity {

    private String deviceName;
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常
     * "
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
    private String name;//设备名称
    private Date generationDataTime;
    @Ignore
    private String time;
    private Integer equipmentId;//设备ID
    private Integer equipChannelStatus;//前置机连接的设备的通道状态	"0-正常，1-异常

    private String phaseRvoltage;//R相市电电压  精度0.1V
    private String phaseSvoltage;//S相市电电压  精度0.1V
    private String phaseTvoltage;//T相市电电压  精度0.1V

    private String frequency;//市电频率 精度0.1V
    private String outVoltage;//输出电压 精度0.1V
    private String shortVoltageH;//短路电压 精度0.1V
    private String outFrequency;//输出频率
    private String outCurrent;//输出电流
    private String battVoltage;//电池电压
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
    private String temperature;//温度  精度0.1℃
    private String battRemainTime;//电池剩余备用时间估计
    private String battSoc;//电池容量百分比
    private Short currRunMode;//当前运行模式
    private Short currBattTestStatus;//当前电池测试状态
    private Short faultCode;//故障代码
    private Short warningCode;//警告代码
    private Short errorCode;//错误代码
    private Short errPowerVoltage;//错误的市电电压
    private String errPowerFrequency;//错误的市电频率
    private Short errInvVoltage;//错误的逆变电压
    private String errInvFrequency;//错误的逆变频率
    private Short errLoader;//错误的负载

    private Short errOutCurrentP;//错误的输出电流百分比
    private Short errPbusVoltage;//错误的PBUS电压
    private Short errNbusVoltage;//错误的NBUS电压
    private String errBattVoltage;//错误的电池电压
    private String errTemperature;//错误的温度
    private Short errPfcStatus;//错误的PFC状态
    private Short errInvStatus;//错误的逆变状态
    private Short errRectStatus;//错误的整流状态
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
