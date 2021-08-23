package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.BcuDiStatusAlarmConverter;
import com.bugull.hithiumfarmweb.http.excelConverter.EquipStatusConverter;
import com.bugull.hithiumfarmweb.http.excelConverter.PcsExceptionConverter;
import com.bugull.hithiumfarmweb.http.excelConverter.PcsFaultConverter;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 消防
 */
@Data
@Entity
@EnsureIndex("{deviceName:1,equipmentId:1},{generationDataTime:-1}")
public class FireControlDataDic implements BuguEntity {
    @ExcelIgnore
    @Id
    private String id;
    //储能站设备唯一标识
    @ExcelIgnore
    private String deviceName;
    //设备名称
    @ExcelProperty(value = "设备名称")
    private String name;
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常
     * IsMasterPowerFault	bool	主电状态
     * IsAuxPowerFault	bool	备电状态
     * IsCommFault	bool	通讯状态
     * IsMasterValveFault1	bool	主阀1故障状态
     * IsMasterValveOpen1	bool	主阀1开关状态
     * IsMasterValveFault2	bool	主阀2故障状态
     * IsMasterValveOpen2	bool	主阀2开关状态
     * IsZoneValveFault1	bool	主控1分区阀1故障状态
     * IsZoneValveOpen1	bool	主控1分区阀1开关状态
     * IsZoneValveFault2	bool	主控1分区阀2故障状态
     * IsZoneValveOpen2	bool	主控1分区阀2开关状态
     */
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @Ignore
    @ExcelIgnore
    private String time;//时间
    @ExcelIgnore
    private Integer equipmentId;//设备ID
    @ExcelProperty(value = "前置机状态",converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;//前置机连接的设备的通道状态 "0-正常，1-异常
    @ExcelProperty(value = "主电状态",converter = PcsExceptionConverter.class)
    private Boolean isMasterPowerFault;//主电状态
    @ExcelProperty(value = "备电状态",converter = PcsExceptionConverter.class)
    private Boolean isAuxPowerFault;//备电状态
    @ExcelProperty(value = "通讯状态",converter = PcsExceptionConverter.class)
    private Boolean isCommFault;//通讯状态
    @ExcelProperty(value = "主阀1故障状态",converter = PcsFaultConverter.class)
    private Boolean isMasterValveFault1;//主阀1故障状态
    @ExcelProperty(value = "主阀1开关状态",converter = PcsExceptionConverter.class)
    private Boolean isMasterValveOpen1;//主阀1开关状态
    @ExcelProperty(value = "主阀2故障状态",converter = PcsFaultConverter.class)
    private Boolean isMasterValveFault2;//主阀2故障状态
    @ExcelProperty(value = "主阀2开关状态",converter = PcsExceptionConverter.class)
    private Boolean isMasterValveOpen2;//主阀2开关状态
    @ExcelProperty(value = "主控1分区阀1故障状态",converter = PcsFaultConverter.class)
    private Boolean isZoneValveFault1;//主控1分区阀1故障状态
    @ExcelProperty(value = "主控1分区阀1开关状态",converter = PcsExceptionConverter.class)
    private Boolean isZoneValveOpen1;//主控1分区阀1开关状态
    @ExcelProperty(value = "主控1分区阀2故障状态",converter = PcsFaultConverter.class)
    private Boolean isZoneValveFault2;//主控1分区阀2故障状态
    @ExcelProperty(value = "主控1分区阀2开关状态",converter = PcsExceptionConverter.class)
    private Boolean isZoneValveOpen2;//主控1分区阀2开关状态

    /**
     * IsAlarmBellFault1	bool	主控1警铃故障状态
     * IsAlarmBellOpen1	bool	主控1警铃开关状态
     * IsGasLightFault1	bool	主控1放气灯1故障状态
     * IsGasLightOpen1	bool	主控1放气灯1开关状态
     * IsGasLightFault2	bool	主控1放气灯2故障状态
     * IsGasLightOpen2	bool	主控1放气灯2开关状态
     * IsMainBreakerFault1	bool	主控1总闸故障状态
     * IsMainBreakerOpen1	bool	主控1总闸开关状态
     * IsRepFault1	bool	主控1库位1库位状态
     * IsManFireFault1	bool	主控1库位1手报故障状态
     * IsManFireAlarm1	bool	主控1库位1手报告警状态
     */
    @ExcelProperty(value = "主控1警铃故障状态",converter = PcsFaultConverter.class)
    private Boolean isAlarmBellFault1;//主控1警铃故障状态
    @ExcelProperty(value = "主控1警铃开关状态",converter = PcsExceptionConverter.class)
    private Boolean isAlarmBellOpen1;//主控1警铃开关状态
    @ExcelProperty(value = "主控1放气灯1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasLightFault1;//主控1放气灯1故障状态
    @ExcelProperty(value = "主控1放气灯1开关状态",converter = PcsExceptionConverter.class)
    private Boolean isGasLightOpen1;//主控1放气灯1开关状态
    @ExcelProperty(value = "主控1放气灯2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasLightFault2;//主控1放气灯2故障状态
    @ExcelProperty(value = "主控1放气灯2开关状态",converter = PcsExceptionConverter.class)
    private Boolean isGasLightOpen2;//主控1放气灯2开关状态
    @ExcelProperty(value = "主控1总闸故障状态",converter = PcsFaultConverter.class)
    private Boolean isMainBreakerFault1;//主控1总闸故障状态
    @ExcelProperty(value = "主控1总闸开关状态",converter = PcsExceptionConverter.class)
    private Boolean isMainBreakerOpen1;//主控1总闸开关状态
    @ExcelProperty(value = "主控1库位1库位状态",converter = PcsExceptionConverter.class)
    private Boolean isRepFault1;//主控1库位1库位状态
    @ExcelProperty(value = "主控1库位1手报故障状态",converter = PcsFaultConverter.class)
    private Boolean isManFireFault1;//主控1库位1手报故障状态
    @ExcelProperty(value = "主控1库位1手报告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isManFireAlarm1;//主控1库位1手报告警状态
    /**
     * IsSoundLightAlarmFault1	bool	主控1库位1声光报警故障状态
     * IsSoundLightAlarmOpen1	bool	主控1库位1声光报警开关状态
     * IsFlameDetectorFault1	bool	主控1库位1火焰探测器1故障状态
     * IsFlameDetectorAlarm1	bool	主控1库位1火焰探测器1告警状态
     * IsFlameDetectorFault2	bool	主控1库位1火焰探测器2故障状态
     * IsFlameDetectorAlarm2	bool	主控1库位1火焰探测器2告警状态
     * IsGasDetectorFault1	bool	主控1库位1气体探测器1故障状态
     * IsGasDetectorAlarm1	bool	主控1库位1气体探测器1告警状态
     * IsGasDetectorFault2	bool	主控1库位1气体探测器2故障状态
     * IsGasDetectorAlarm2	bool	主控1库位1气体探测器2告警状态
     */
    @ExcelProperty(value = "主控1库位1声光报警故障状态",converter = PcsFaultConverter.class)
    private Boolean isSoundLightAlarmFault1;//主控1库位1声光报警故障状态
    @ExcelProperty(value = "主控1库位1声光报警开关状态",converter = PcsExceptionConverter.class)
    private Boolean isSoundLightAlarmOpen1;//主控1库位1声光报警开关状态
    @ExcelProperty(value = "主控1库位1火焰探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault1;//主控1库位1火焰探测器1故障状态
    @ExcelProperty(value = "主控1库位1火焰探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm1;//主控1库位1火焰探测器1告警状态
    @ExcelProperty(value = "主控1库位1火焰探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault2;//主控1库位1火焰探测器2故障状态
    @ExcelProperty(value = "主控1库位1火焰探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm2;//主控1库位1火焰探测器2告警状态
    @ExcelProperty(value = "主控1库位1气体探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault1;//主控1库位1气体探测器1故障状态
    @ExcelProperty(value = "主控1库位1气体探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm1;//主控1库位1气体探测器1告警状态
    @ExcelProperty(value = "主控1库位1气体探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault2;//主控1库位1气体探测器2故障状态
    @ExcelProperty(value = "主控1库位1气体探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm2;//主控1库位1气体探测器2告警状态
    /**
     * IsRepFault2	bool	主控1库位2库位状态
     * IsManFireFault2	bool	主控1库位2手报故障状态
     * IsManFireAlarm2	bool	主控1库位2手报告警状态
     * IsSoundLightAlarmFault2	bool	主控1库位2声光报警故障状态
     * IsSoundLightAlarmOpen2	bool	主控1库位2声光报警开关状态
     * IsFlameDetectorFault3	bool	主控1库位2火焰探测器1故障状态
     * IsFlameDetectorAlarm3	bool	主控1库位2火焰探测器1告警状态
     * IsFlameDetectorFault4	bool	主控1库位2火焰探测器2故障状态
     * IsFlameDetectorAlarm4	bool	主控1库位2火焰探测器2告警状态
     * IsGasDetectorFault3	bool	主控1库位2气体探测器1故障状态
     * IsGasDetectorAlarm3	bool	主控1库位2气体探测器1告警状态
     * IsGasDetectorFault4	bool	主控1库位2气体探测器2故障状态
     * IsGasDetectorAlarm4	bool	主控1库位2气体探测器2告警状态
     */
    @ExcelProperty(value = "主控1库位2库位状态",converter = PcsExceptionConverter.class)
    private Boolean isRepFault2;//主控1库位2库位状态
    @ExcelProperty(value = "主控1库位2手报故障状态",converter = PcsFaultConverter.class)
    private Boolean isManFireFault2;//主控1库位2手报故障状态
    @ExcelProperty(value = "主控1库位2手报告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isManFireAlarm2;//主控1库位2手报告警状态
    @ExcelProperty(value = "主控1库位2声光报警故障状态",converter = PcsFaultConverter.class)
    private Boolean isSoundLightAlarmFault2;//主控1库位2声光报警故障状态
    @ExcelProperty(value = "主控1库位2声光报警开关状态",converter = PcsExceptionConverter.class)
    private Boolean isSoundLightAlarmOpen2;//主控1库位2声光报警开关状态
    @ExcelProperty(value = "主控1库位2火焰探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault3;//主控1库位2火焰探测器1故障状态
    @ExcelProperty(value = "主控1库位2火焰探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm3;//主控1库位2火焰探测器1告警状态
    @ExcelProperty(value = "主控1库位2火焰探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault4;//主控1库位2火焰探测器2故障状态
    @ExcelProperty(value = "主控1库位2火焰探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm4;//主控1库位2火焰探测器2告警状态
    @ExcelProperty(value = "主控1库位2气体探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault3;//主控1库位2气体探测器1故障状态
    @ExcelProperty(value = "主控1库位2气体探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm3;//主控1库位2气体探测器1告警状态
    @ExcelProperty(value = "主控1库位2气体探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault4;//主控1库位2气体探测器2故障状态
    @ExcelProperty(value = "主控1库位2气体探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm4;//主控1库位2气体探测器2告警状态

    /**
     * IsZoneValveFault3	bool	主控2分区阀1故障状态
     * IsZoneValveOpen3	bool	主控2分区阀1开关状态
     * IsZoneValveFault4	bool	主控2分区阀2故障状态
     * IsZoneValveOpen4	bool	主控2分区阀2开关状态
     * IsAlarmBellFault2	bool	主控2警铃故障状态
     * IsAlarmBellOpen2	bool	主控2警铃开关状态
     * IsGasLightFault3	bool	主控2放气灯1故障状态
     * IsGasLightOpen3	bool	主控2放气灯1开关状态
     * IsGasLightFault4	bool	主控2放气灯2故障状态
     * IsGasLightOpen4	bool	主控2放气灯2开关状态
     * IsMainBreakerFault2	bool	主控2总闸故障状态
     * * IsMainBreakerOpen2	bool	主控2总闸开关状态
     */
    @ExcelProperty(value = "主控2分区阀1故障状态",converter = PcsFaultConverter.class)
    private Boolean isZoneValveFault3;//主控2分区阀1故障状态
    @ExcelProperty(value = "主控2分区阀1开关状态",converter = PcsExceptionConverter.class)
    private Boolean isZoneValveOpen3;//主控2分区阀1开关状态
    @ExcelProperty(value = "主控2分区阀2故障状态",converter = PcsFaultConverter.class)
    private Boolean isZoneValveFault4;//主控2分区阀2故障状态
    @ExcelProperty(value = "主控2分区阀2开关状态",converter = PcsExceptionConverter.class)
    private Boolean isZoneValveOpen4;//主控2分区阀2开关状态
    @ExcelProperty(value = "主控2警铃故障状态",converter = PcsFaultConverter.class)
    private Boolean isAlarmBellFault2;//主控2警铃故障状态
    @ExcelProperty(value = "主控2警铃开关状态",converter = PcsExceptionConverter.class)
    private Boolean isAlarmBellOpen2;//主控2警铃开关状态
    @ExcelProperty(value = "主控2放气灯1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasLightFault3;//主控2放气灯1故障状态
    @ExcelProperty(value = "主控2放气灯1开关状态",converter = PcsExceptionConverter.class)
    private Boolean isGasLightOpen3;//主控2放气灯1开关状态
    @ExcelProperty(value = "主控2放气灯2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasLightFault4;//主控2放气灯2故障状态
    @ExcelProperty(value = "主控2放气灯2开关状态",converter = PcsExceptionConverter.class)
    private Boolean isGasLightOpen4;//主控2放气灯2开关状态
    @ExcelProperty(value = "主控2总闸故障状态",converter = PcsFaultConverter.class)
    private Boolean isMainBreakerFault2;//主控2总闸故障状态
    @ExcelProperty(value = "主控2总闸开关状态",converter = PcsExceptionConverter.class)
    private Boolean isMainBreakerOpen2;//主控2总闸开关状态


    /**
     * IsRepFault3	bool	主控2库位1库位状态
     * IsManFireFault3	bool	主控2库位1手报故障状态
     * IsManFireAlarm3	bool	主控2库位1手报告警状态
     * IsSoundLightAlarmFault3	bool	主控2库位1声光报警故障状态
     * IsSoundLightAlarmOpen3	bool	主控2库位1声光报警开关状态
     * IsFlameDetectorFault5	bool	主控2库位1火焰探测器1故障状态
     * IsFlameDetectorAlarm5	bool	主控2库位1火焰探测器1告警状态
     * IsFlameDetectorFault6	bool	主控2库位1火焰探测器2故障状态
     * IsFlameDetectorAlarm6	bool	主控2库位1火焰探测器2告警状态
     */
    @ExcelProperty(value = "主控2库位1库位状态",converter = PcsExceptionConverter.class)
    private Boolean isRepFault3;//主控2库位1库位状态
    @ExcelProperty(value = "主控2库位1手报故障状态",converter = PcsFaultConverter.class)
    private Boolean isManFireFault3;//主控2库位1手报故障状态
    @ExcelProperty(value = "主控2库位1手报告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isManFireAlarm3;//主控2库位1手报告警状态
    @ExcelProperty(value = "主控2库位1声光报警故障状态",converter = PcsFaultConverter.class)
    private Boolean isSoundLightAlarmFault3;//主控2库位1声光报警故障状态
    @ExcelProperty(value = "主控2库位1声光报警开关状态",converter = PcsExceptionConverter.class)
    private Boolean isSoundLightAlarmOpen3;//主控2库位1声光报警开关状态
    @ExcelProperty(value = "主控2库位1火焰探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault5;//主控2库位1火焰探测器1故障状态
    @ExcelProperty(value = "主控2库位1火焰探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm5;//主控2库位1火焰探测器1告警状态
    @ExcelProperty(value = "主控2库位1火焰探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault6;//主控2库位1火焰探测器2故障状态
    @ExcelProperty(value = "主控2库位1火焰探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm6;//主控2库位1火焰探测器2告警状态
    /**
     * IsGasDetectorFault5	bool	主控2库位1气体探测器1故障状态
     * IsGasDetectorAlarm5	bool	主控2库位1气体探测器1告警状态
     * IsGasDetectorFault6	bool	主控2库位1气体探测器2故障状态
     * IsGasDetectorAlarm6	bool	主控2库位1气体探测器2告警状态
     * IsRepFault4	bool	主控2库位2库位状态
     * IsManFireFault4	bool	主控2库位2手报故障状态
     * IsManFireAlarm4	bool	主控2库位2手报告警状态
     * IsSoundLightAlarmFault4	bool	主控2库位2声光报警故障状态
     * IsSoundLightAlarmOpen4	bool	主控2库位2声光报警开关状态
     * IsFlameDetectorFault7	bool	主控2库位2火焰探测器1故障状态
     * IsFlameDetectorAlarm7	bool	主控2库位2火焰探测器1告警状态
     * IsFlameDetectorFault8	bool	主控2库位2火焰探测器2故障状态
     * IsFlameDetectorAlarm8	bool	主控2库位2火焰探测器2告警状态
     */
    @ExcelProperty(value = "主控2库位1气体探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault5;//主控2库位1气体探测器1故障状态
    @ExcelProperty(value = "主控2库位1气体探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm5;//主控2库位1气体探测器1告警状态
    @ExcelProperty(value = "主控2库位1气体探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault6;//主控2库位1气体探测器2故障状态
    @ExcelProperty(value = "主控2库位1气体探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm6;//主控2库位1气体探测器2告警状态
    @ExcelProperty(value = "主控2库位2库位状态",converter = PcsExceptionConverter.class)
    private Boolean isRepFault4;//主控2库位2库位状态
    @ExcelProperty(value = "主控2库位2手报故障状态",converter = PcsFaultConverter.class)
    private Boolean isManFireFault4;//主控2库位2手报故障状态
    @ExcelProperty(value = "主控2库位2手报告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isManFireAlarm4;//主控2库位2手报告警状态
    @ExcelProperty(value = "主控2库位2声光报警故障状态",converter = PcsFaultConverter.class)
    private Boolean isSoundLightAlarmFault4;//主控2库位2声光报警故障状态
    @ExcelProperty(value = "主控2库位2声光报警开关状态",converter = PcsExceptionConverter.class)
    private Boolean isSoundLightAlarmOpen4;//主控2库位2声光报警开关状态
    @ExcelProperty(value = "主控2库位2火焰探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault7;//主控2库位2火焰探测器1故障状态
    @ExcelProperty(value = "主控2库位2火焰探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm7;//主控2库位2火焰探测器1告警状态
    @ExcelProperty(value = "主控2库位2火焰探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isFlameDetectorFault8;//主控2库位2火焰探测器2故障状态
    @ExcelProperty(value = "主控2库位2火焰探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isFlameDetectorAlarm8;//主控2库位2火焰探测器2告警状态
    @ExcelProperty(value = "主控2库位2气体探测器1故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault7;//主控2库位2气体探测器1故障状态
    @ExcelProperty(value = "主控2库位2气体探测器1告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm7;//主控2库位2气体探测器1告警状态
    @ExcelProperty(value = "主控2库位2气体探测器2故障状态",converter = PcsFaultConverter.class)
    private Boolean isGasDetectorFault8;//主控2库位2气体探测器2故障状态
    @ExcelProperty(value = "主控2库位2气体探测器2告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean isGasDetectorAlarm8;//主控2库位2气体探测器2告警状态
    @ExcelProperty(value = "消防总控信息")
    private Integer masterCtrlInfo;//消防总控信息
    @ExcelProperty(value = "主控1信息")
    private Integer mainCtrlInfo1;//主控1信息
    @ExcelProperty(value = "主控1库位1信息")
    private Integer fireRepInfo1;//主控1库位1信息
    @ExcelProperty(value = "主控1库位2信息")
    private Integer fireRepInfo2;//主控1库位2信息
    @ExcelProperty(value = "主控2信息")
    private Integer mainCtrlInfo2;//主控2信息
    @ExcelProperty(value = "主控2库位1信息")
    private Integer fireRepInfo3;//主控2库位1信息
    @ExcelProperty(value = "主控2库位2信息")
    private Integer fireRepInfo4;//主控2库位2信息
    /** IsGasDetectorFault7	bool	主控2库位2气体探测器1故障状态
     * IsGasDetectorAlarm7	bool	主控2库位2气体探测器1告警状态
     * IsGasDetectorFault8	bool	主控2库位2气体探测器2故障状态
     * IsGasDetectorAlarm8	bool	主控2库位2气体探测器2告警状态
     * MasterCtrlInfo	ushort	消防总控信息
     * MainCtrlInfo1	ushort	主控1信息
     * FireRepInfo1	ushort	主控1库位1信息
     * FireRepInfo2	ushort	主控1库位2信息
     * MainCtrlInfo2	ushort	主控2信息
     * FireRepInfo3	ushort	主控2库位1信息
     * FireRepInfo4	ushort	主控2库位2信息
     */
}
