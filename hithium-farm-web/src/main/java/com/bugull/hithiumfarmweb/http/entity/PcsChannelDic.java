package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 通道
 */
@Data
@Entity
public class PcsChannelDic extends SimpleEntity {

    private String deviceName;
   
    private String name;//设备名称
    @Ignore
    private String time; //时间
    private Integer equipmentId;//设备ID
    //0-正常，1-异常
    //默认值异常  前置机连接的设备的通道状态
    private Integer equipChannelStatus;
    private Date generationDataTime;
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常
     * "
     * PowerOnOff	ushort	模块开关机
     * FaultClear	ushort	模块故障清除
     * AverageChargeVoltage	ushort	电池均充电压
     * StringChargeVoltage	ushort	电池浮充电压
     * MaxChargeCurrent	ushort	最大充电电流
     * MaxDischargeCurrent	ushort	最大放电电流
     * AvgGotoStringChargeCurrent	ushort	均充转浮充电流
     * VoltageProtectHighLimit	ushort	电压保护上限
     * VoltageProtectLowLimit	ushort	电压保护下限
     * EodVoltage	ushort	EOD电压
     * JoinPowerGridDod	ushort	并网DOD
     * OffPowerGridDod	ushort	离网DOD
     */
    private Short powerOnOff; //模块开关机
    private Short faultClear; //模块故障清除
    private Short averageChargeVoltage; //电池均充电压
    private Short floatChargeVoltage; //电池浮充电压
    private Short maxChargeCurrent; //最大充电电流
    private Short maxDischargeCurrent;//最大放电电流

    private Short avgGotoFlotChargeCurrent;    //均充转浮充电流
    private Short voltageProtectHighLimit;  //电压保护上限
    private Short voltageProtectLowLimit;   //电压保护下限
    private Short eodVoltage;//EOD电压
    private Short joinPowerGridDod;//并网DOD
    private Short offPowerGridDod;//离网DOD

    /**
     * ModelEnable	ushort	模块使能
     * BmsAlarmEnable	ushort	BMS告警使能
     * BatteryType	ushort	电池类型	"        [Description(""未知类型"")] 0=Unknown,
     * [Description(""钛酸锂电池"")] 1=Li2TiO3,
     * [Description(""磷酸铁锂电池"")] 2=LiFePO4"
     */
    private Short modelEnable;//模块使能
    private Short bmsAlarmEnable;//BMS告警使能
    private Short batteryType;//电池类型   0:Unknown 1=钛酸锂电池  2=磷酸铁锂电池

    /**
     * BatteryCapacity	ushort	电池容量
     * ActivePower	int	有功功率
     * ReactivePower	int	无功功率
     * FaultWord1	ushort	故障(状态)字1
     * FaultWord2	ushort	故障(状态)字2
     * FaultWord3	ushort	故障(状态)字3
     * FaultWord4	ushort	故障(状态)字4
     * FaultWord5	ushort	故障(状态)字5
     * FaultWord6	ushort	故障(状态)字6
     * FaultWord7	ushort	故障(状态)字7
     * FaultStatus	bool	故障状态
     * AlarmStatus	bool	 告警状态
     * DeviceOnline	bool	 设备在线
     * ChargeDischargeStatus	short	 充放电状态	0-待机，1-充电，2-放电
     * DisableCharge	bool	 禁止充电
     * DisableDischarge	bool	 禁止放电
     * RunningStatus	bool	 运行状态	false-关机，true-开机
     */
    private Short batteryCapacity;//电池容量
    private Integer activePower;//有功功率
    private Integer reactivePower;//无功功率
    private Short faultWord1;//故障状态 1
    private Short faultWord2;//故障状态 2
    private Short faultWord3;//故障状态 3
    private Short faultWord4;//故障状态 4
    private Short faultWord5;//故障状态 5
    private Short faultWord6;//故障状态 6
    private Short faultWord7;//故障状态 7
    private Boolean faultStatus;//故障状态
    private Boolean alarmStatus;//告警状态
    private Boolean deviceOnline;//设备在线
    private Short chargeDischargeStatus;//充放电状态 0:待机 1:充电 2:放电
    private Boolean disableCharge;//禁止充电
    private Boolean disableDischarge;//禁止放电
    private Boolean runningStatus;//运行状态 false:关机 true:开机
    /**
     * InEnvHighTemp	bool	 内部环温过高故障
     * BmsCommFault	bool	 BMS通讯故障
     * DcReverse	bool	 直流反接故障
     * DcOverCurrent	bool	 直流电流过流故障
     * OverLoadAlarm	bool	 过载告警
     * DcOverVoltage	bool	 直流过压故障
     * DcUnderVoltage	bool	 直流欠压故障
     * NoneBattery	bool	 无电池故障
     * DcInputOverVolt	bool	 直流输入过压告警
     * DcInputUnderVolt	bool	 直流输入欠压告警
     * InternalCanFault	bool	 内部CAN通信故障
     * ElecVoltException	bool	 市电电压异常故障
     * AcOverCurrProtect	bool	 交流过流保护故障
     * PowerGridReverse	bool	 电网反序故障
     */
    private Boolean inEnvHighTemp;//内部环温过高故障
    private Boolean bmsCommFault;//BMS通讯故障
    private Boolean dcReverse;//直流反接故障
    private Boolean dcOverCurrent;//直流电流过流故障
    private Boolean overLoadAlarm;//过载告警
    private Boolean dcOverVoltage;//直流过压故障
    private Boolean dcUnderVoltage;//直流欠压故障
    private Boolean noneBattery;//无电池故障
    private Boolean dcInputOverVolt;//直流输入过压告警
    private Boolean dcInputUnderVolt;//直流输入欠压告警
    private Boolean internalCanFault;//内部CAN通信故障
    private Boolean elecVoltException;//市电电压异常故障
    private Boolean acOverCurrProtect;//交流过流保护故障
    private Boolean powerGridReverse;//电网反序故障
    /** OutputOverLoadTime	bool	 输出过载超时故障
     * EnvTempDerating	bool	 环温降额告警
     * ParallelCanCommFault	bool	 并机CAN通讯故障
     * AuxPowerFault	bool	 辅源故障
     * FanFault	bool	 风扇故障
     * MainLineFault	bool	 母线故障
     * InverterMainLineVoltUnbalance	bool	 逆变器母线电压不平衡故障
     * InverterMainLineSoftStartFailed	bool	 逆变器母线软起失败故障
     * ModelCanCommFault	bool	 模块CAN通信故障
     * InternalSpiCommFault	bool	 内部SPI通讯故障
     * DipSwitchAddrError	bool	 拨码地址错误故障
     * NoCtrlRectifierAlarm	bool	 不控整流告警
     * LowFreqSyncFail	bool	 低频同步失败故障*/
    private Boolean outputOverLoadTime;//输出过载超时故障
    private Boolean envTempDerating;//环温降额告警
    private Boolean parallelCanCommFault;//并机CAN通讯故障
    private Boolean auxPowerFault;//辅源故障
    private Boolean fanFault;//风扇故障
    private Boolean mainLineFault;//母线故障
    private Boolean inverterMainLineVoltUnbalance;//逆变器母线电压不平衡故障
    private Boolean inverterMainLineSoftStartFailed;//逆变器母线软起失败故障
    private Boolean modelCanCommFault;//模块CAN通信故障
    private Boolean internalSpiCommFault;//内部SPI通讯故障
    private Boolean dipSwitchAddrError;//拨码地址错误故障
    private Boolean noCtrlRectifierAlarm;//不控整流告警
    private Boolean lowFreqSyncFail;//低频同步失败故障
    /** DCPower	String	 直流功率	默认值是浮点数-9999f
     * DCVoltage	String	 直流电压	默认值是浮点数-9999f
     * DCCurrent	String	 直流电流	默认值是浮点数-9999f
     * DCRadiatorTemp	String	 直流散热器温度	默认值是浮点数-9999f
     * MainlineVolPos	String	 母线电压正	默认值是浮点数-9999f
     * MainlineVolNeg	String	 母线电压负	默认值是浮点数-9999f
     * BmsWorkStatus	int	 BMS工作状态	"
     * 默认值-1即断开
     *         [Description(""断开"")]
     *         Disconnected = -1,
     *         [Description(""充满"")]
     *         FullyCharged = 0,
     *         [Description(""放空"")]
     *         EmptyDischarged = 1,
     *         [Description(""告警"")]
     *         Warning = 2,
     *         [Description(""故障"")]
     *         Fault = 3,
     *         [Description(""正常"")]
     *         Normal = 4,
     *         [Description(""预警"")]
     *         EarlyWarning = 5,
     *         [Description(""跳机"")]
     *         TripOff = 6,
     *         [Description(""待机"")]
     *         StandBy = 7,"*/
    private String dCPower;//直流功率
    private String dCVoltage;//直流电压
    private String dCCurrent;//直流电流
    private String dCRadiatorTemp;//直流散热器温度
    private String mainlineVolPos;//母线电压正
    private String mainlineVolNeg;//母线电压负
    private Integer BmsWorkStatus;//BMS工作状态 -1:断开 0:充满 1:放空 2:告警 3:故障 4:正常 5:预警 6:跳机 7:待机

    /** BClusterVol	String	 电池簇电压	默认值是浮点数-9999f
     * BClusterCur	String	 电池簇电流	默认值是浮点数-9999f
     * GroupRechargeableQuantity	String	 电池簇可充电量	默认值是浮点数-9999f
     * Soc	String	 电池簇SOC	默认值是浮点数-9999f
     * GroupRedischargeableQuantity	String	 电池簇可放电量	默认值是浮点数-9999f
     * MinVoltage	String	 单体最低电池电压	默认值是浮点数-9999f
     * MaxVoltage	String	 单体最高电池电压	默认值是浮点数-9999f
     * MaxSoc	String	 电池簇最高SOC	默认值是浮点数-9999f
     * MinSoc	String	 电池簇最低SOC	默认值是浮点数-9999f
     * MinTemp	String	 单体最低电池温度	默认值是浮点数-9999f
     * MaxTemp	String	 单体最高电池温度	默认值是浮点数-9999f
     * AVoltage	String	 A相相电压	默认值是浮点数-9999f
     * BVoltage	String	 B相相电压	默认值是浮点数-9999f
     * CVoltage	String	 C相相电压	默认值是浮点数-9999f
     * ABVoltage	String	 AB相线电压	默认值是浮点数-9999f
     * BCVoltage	String	 BC相线电压	默认值是浮点数-9999f
     * CAVoltage	String	 CA相线电压	默认值是浮点数-9999f*/
    private String bClusterVol;//电池簇电压
    private String bClusterCur;//电池簇电流
    private String groupRechargeableQuantity;//电池簇可充电量
    private String soc;//电池簇SOC
    private String groupRedischargeableQuantity;//电池簇可放电量
    private String minVoltage;//单体最低电池电压
    private String maxVoltage;//单体最高电池电压
    private String maxSoc;//电池簇最高SOC
    private String minSoc;//电池簇最低SOC
    private String minTemp;//单体最低电池温度
    private String maxTemp;//单体最高电池温度
    private String aVoltage;//A相相电压
    private String bVoltage;//B相相电压
    private String cVoltage;//C相相电压
    private String aBVoltage;//AB相线电压
    private String bCVoltage;//BC相线电压
    private String cAVoltage;//CA相线电压
    /** ARate	String	 A相电网频率	默认值是浮点数-9999f
     * BRate	String	 B相电网频率	默认值是浮点数-9999f
     * CRate	String	 C相电网频率	默认值是浮点数-9999f
     * AFactor	String	 A相功率因数	默认值是浮点数-9999f
     * BFactor	String	 B相功率因数	默认值是浮点数-9999f
     * CFactor	String	 C相功率因数	默认值是浮点数-9999f
     * ACurrent	String	 A相输出电流	默认值是浮点数-9999f
     * BCurrent	String	 B相输出电流	默认值是浮点数-9999f
     * CCurrent	String	 C相输出电流	默认值是浮点数-9999f
     * APower	String	 A相有功功率	默认值是浮点数-9999f
     * BPower	String	 B相有功功率	默认值是浮点数-9999f
     * CPower	String	 C相有功功率	默认值是浮点数-9999f*/
    private String aRate;//A相电网频率
    private String bRate;//B相电网频率
    private String cRate;//C相电网频率
    private String aFactor;//A相功率因数
    private String bFactor;//B相功率因数
    private String cFactor;//C相功率因数
    private String aCurrent;//A相输出电流
    private String bCurrent;//B相输出电流
    private String cCurrent;//C相输出电流
    private String aPower;//A相有功功率
    private String bPower;//B相有功功率
    private String cPower;//C相有功功率

    private String aReactivePower;//A相无功功率
    private String bReactivePower;//B相无功功率
    private String cReactivePower;//C相无功功率
    private String aApparentPower;//A相视在功率
    private String bApparentPower;//B相视在功率
    private String cApparentPower;//C相视在功率
    private String sumPower;//三相总有功功率
    private String sumRelativePower;//三相总无功功率
    private String sumApparentPower;//三相总视在功率
    private String sumPowerFactor;//三相总功率因数
    private String iNVMainlineVolPos;//INV正母线电压
    private String iNVMainlineVolNeg;//INV负母线电压
    private String pcsRadiatorTemp;//逆变侧散热器温度
    private String desiredPower;//有功功率期望
    private String desiredReactivePower;//无功功率期望
    private String maxChargePower;//最大允许充电功率
    private String maxDischargePower;//最大允许放电功率
     /** AReactivePower	String	 A相无功功率	默认值是浮点数-9999f
     * BReactivePower	String	 B相无功功率	默认值是浮点数-9999f
     * CReactivePower	String	 C相无功功率	默认值是浮点数-9999f
     * AApparentPower	String	 A相视在功率	默认值是浮点数-9999f
     * BApparentPower	String	 B相视在功率	默认值是浮点数-9999f
     * CApparentPower	String	 C相视在功率	默认值是浮点数-9999f
     * SumPower	String	 三相总有功功率	默认值是浮点数-9999f
     * SumRelativePower	String	 三相总无功功率	默认值是浮点数-9999f
     * SumApparentPower	String	 三相总视在功率	默认值是浮点数-9999f
     * SumPowerFactor	String	 三相总功率因数	默认值是浮点数-9999f
     * INVMainlineVolPos	String	 INV正母线电压	默认值是浮点数-9999f
     * INVMainlineVolNeg	String	 INV负母线电压	默认值是浮点数-9999f
     * PcsRadiatorTemp	String	 逆变侧散热器温度	默认值是浮点数-9999f
     * DesiredPower	String	 有功功率期望	默认值是浮点数-9999f
     * DesiredReactivePower	String	 无功功率期望	默认值是浮点数-9999f
     * MaxChargePower	String	 最大允许充电功率	默认值是浮点数-9999f
     * MaxDischargePower	String	 最大允许放电功率	默认值是浮点数-9999f
     */
}
