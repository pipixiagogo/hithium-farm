package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.*;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 通道
 */
@Data
@Entity
@EnsureIndex("{deviceName:1,equipmentId:1},{generationDataTime:1}")
public class PcsChannelDic implements BuguEntity {

    @ExcelIgnore
    @Id
    private String id;
    @ExcelIgnore
    private String deviceName;
    @ExcelProperty(value = "设备名称")
    private String name;//设备名称
    @Ignore
    @ExcelIgnore
    private String time; //时间
    @ExcelIgnore
    private Integer equipmentId;//设备ID
    //0-正常，1-异常
    //默认值异常  前置机连接的设备的通道状态
    @ExcelProperty(value = "前置机通道状态", converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;
    @ExcelProperty(value = "时间")
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
    @ExcelProperty(value = "模块开关机")
    private Short powerOnOff; //模块开关机
    @ExcelProperty(value = "模块故障清除")
    private Short faultClear; //模块故障清除
    @ExcelProperty(value = "电池均充电压")
    private Short averageChargeVoltage; //电池均充电压
    @ExcelProperty(value = "电池浮充电压")
    private Short floatChargeVoltage; //电池浮充电压
    @ExcelProperty(value = "最大充电电流")
    private Short maxChargeCurrent; //最大充电电流
    @ExcelProperty(value = "最大放电电流")
    private Short maxDischargeCurrent;//最大放电电流
    @ExcelProperty(value = "均充转浮充电流")
    private Short avgGotoFloatChargeCurrent;//均充转浮充电流
    @ExcelProperty(value = "电压保护上限")
    private Short voltageProtectHighLimit;  //电压保护上限
    @ExcelProperty(value = "电压保护下限")
    private Short voltageProtectLowLimit;   //电压保护下限
    @ExcelProperty(value = "EOD电压")
    private Short eodVoltage;//EOD电压
    @ExcelProperty(value = "并网DOD")
    private Short joinPowerGridDod;//并网DOD
    @ExcelProperty(value = "离网DOD")
    private Short offPowerGridDod;//离网DOD

    /**
     * ModelEnable	ushort	模块使能
     * BmsAlarmEnable	ushort	BMS告警使能
     * BatteryType	ushort	电池类型	"        [Description(""未知类型"")] 0=Unknown,
     * [Description(""钛酸锂电池"")] 1=Li2TiO3,
     * [Description(""磷酸铁锂电池"")] 2=LiFePO4"
     */
    @ExcelProperty(value = "模块使能")
    private Short modelEnable;//模块使能
    @ExcelProperty(value = "BMS告警使能")
    private Short bmsAlarmEnable;//BMS告警使能
//
    @ExcelProperty(value = "电池类型",converter = PcsBatterTypeConverter.class)
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
    @ExcelProperty(value = "电池容量")
    private Short batteryCapacity;//电池容量
    @ExcelProperty(value = "有功功率")
    private Integer activePower;//有功功率
    @ExcelProperty(value = "无功功率")
    private Integer reactivePower;//无功功率
    @ExcelProperty(value = "故障状态 1")
    private Short faultWord1;//故障状态 1
    @ExcelProperty(value = "故障状态 2")
    private Short faultWord2;//故障状态 2
    @ExcelProperty(value = "故障状态 3")
    private Short faultWord3;//故障状态 3
    @ExcelProperty(value = "故障状态 4")
    private Short faultWord4;//故障状态 4
    @ExcelProperty(value = "故障状态 5")
    private Short faultWord5;//故障状态 5
    @ExcelProperty(value = "故障状态 6")
    private Short faultWord6;//故障状态 6
    @ExcelProperty(value = "故障状态 7")
    private Short faultWord7;//故障状态 7
    @ExcelProperty(value = "故障状态",converter = PcsFaultConverter.class)
    private Boolean faultStatus;//故障状态
    @ExcelProperty(value = "告警状态",converter = BcuDiStatusAlarmConverter.class)
    private Boolean alarmStatus;//告警状态
    @ExcelProperty(value = "设备在线",converter = PcsDeviceOnlineConverter.class)
    private Boolean deviceOnline;//设备在线
    @ExcelProperty(value = "充放电状态",converter = PcsChargeDischargeShortConverter.class)
    private Short chargeDischargeStatus;//充放电状态 0:待机 1:充电 2:放电
    @ExcelProperty(value = "禁止充电",converter = PcsChargeSwitchConverter.class)
    private Boolean disableCharge;//禁止充电
    @ExcelProperty(value = "禁止放电",converter = PcsChargeSwitchConverter.class)
    private Boolean disableDischarge;//禁止放电
    @ExcelProperty(value = "运行状态" ,converter = PcsRunningStatusConverter.class)
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
    @ExcelProperty(value = "内部环温过高故障",converter = PcsFaultConverter.class)
    private Boolean inEnvHighTemp;//内部环温过高故障
    @ExcelProperty(value = "BMS通讯故障",converter = PcsFaultConverter.class)
    private Boolean bmsCommFault;//BMS通讯故障
    @ExcelProperty(value = "直流反接故障",converter = PcsFaultConverter.class)
    private Boolean dcReverse;//直流反接故障
    @ExcelProperty(value = "直流电流过流故障",converter = PcsFaultConverter.class)
    private Boolean dcOverCurrent;//直流电流过流故障
    @ExcelProperty(value = "过载告警",converter = BcuDiStatusAlarmConverter.class)
    private Boolean overLoadAlarm;//过载告警
    @ExcelProperty(value = "直流过压故障",converter = PcsFaultConverter.class)
    private Boolean dcOverVoltage;//直流过压故障
    @ExcelProperty(value = "直流欠压故障",converter = PcsFaultConverter.class)
    private Boolean dcUnderVoltage;//直流欠压故障
    @ExcelProperty(value = "无电池故障",converter = PcsFaultConverter.class)
    private Boolean noneBattery;//无电池故障
    @ExcelProperty(value = "直流输入过压告警",converter = BcuDiStatusAlarmConverter.class)
    private Boolean dcInputOverVolt;//直流输入过压告警
    @ExcelProperty(value = "直流输入欠压告警",converter = BcuDiStatusAlarmConverter.class)
    private Boolean dcInputUnderVolt;//直流输入欠压告警
    @ExcelProperty(value = "内部CAN通信故障",converter = PcsFaultConverter.class)
    private Boolean internalCanFault;//内部CAN通信故障
    @ExcelProperty(value = "市电电压异常故障",converter = PcsFaultConverter.class)
    private Boolean elecVoltException;//市电电压异常故障
    @ExcelProperty(value = "交流过流保护故障",converter = PcsFaultConverter.class)
    private Boolean acOverCurrProtect;//交流过流保护故障
    @ExcelProperty(value = "电网反序故障",converter = PcsFaultConverter.class)
    private Boolean powerGridReverse;//电网反序故障
    /**
     * OutputOverLoadTime	bool	 输出过载超时故障
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
     * LowFreqSyncFail	bool	 低频同步失败故障
     */
    @ExcelProperty(value = "输出过载超时故障",converter = PcsFaultConverter.class)
    private Boolean outputOverLoadTime;//输出过载超时故障
    @ExcelProperty(value = "环温降额告警",converter = BcuDiStatusAlarmConverter.class)
    private Boolean envTempDerating;//环温降额告警
    @ExcelProperty(value = "并机CAN通讯故障",converter = PcsFaultConverter.class)
    private Boolean parallelCanCommFault;//并机CAN通讯故障
    @ExcelProperty(value = "辅源故障",converter = PcsFaultConverter.class)
    private Boolean auxPowerFault;//辅源故障
    @ExcelProperty(value = "风扇故障",converter = PcsFaultConverter.class)
    private Boolean fanFault;//风扇故障
    @ExcelProperty(value = "母线故障",converter = PcsFaultConverter.class)
    private Boolean mainLineFault;//母线故障
    @ExcelProperty(value = "逆变器母线电压不平衡故障",converter = PcsFaultConverter.class)
    private Boolean inverterMainLineVoltUnbalance;//逆变器母线电压不平衡故障
    @ExcelProperty(value = "逆变器母线软起失败故障",converter = PcsFaultConverter.class)
    private Boolean inverterMainLineSoftStartFailed;//逆变器母线软起失败故障
    @ExcelProperty(value = "模块CAN通信故障",converter = PcsFaultConverter.class)
    private Boolean modelCanCommFault;//模块CAN通信故障
    @ExcelProperty(value = "内部SPI通讯故障",converter = PcsFaultConverter.class)
    private Boolean internalSpiCommFault;//内部SPI通讯故障
    @ExcelProperty(value = "拨码地址错误故障",converter = PcsFaultConverter.class)
    private Boolean dipSwitchAddrError;//拨码地址错误故障
    @ExcelProperty(value = "不控整流告警",converter = BcuDiStatusAlarmConverter.class)
    private Boolean noCtrlRectifierAlarm;//不控整流告警
    @ExcelProperty(value = "低频同步失败故障",converter = PcsFaultConverter.class)
    private Boolean lowFreqSyncFail;//低频同步失败故障
    /**
     * DCPower	String	 直流功率	默认值是浮点数-9999f
     * DCVoltage	String	 直流电压	默认值是浮点数-9999f
     * DCCurrent	String	 直流电流	默认值是浮点数-9999f
     * DCRadiatorTemp	String	 直流散热器温度	默认值是浮点数-9999f
     * MainlineVolPos	String	 母线电压正	默认值是浮点数-9999f
     * MainlineVolNeg	String	 母线电压负	默认值是浮点数-9999f
     * BmsWorkStatus	int	 BMS工作状态	"
     * 默认值-1即断开
     * [Description(""断开"")]
     * Disconnected = -1,
     * [Description(""充满"")]
     * FullyCharged = 0,
     * [Description(""放空"")]
     * EmptyDischarged = 1,
     * [Description(""告警"")]
     * Warning = 2,
     * [Description(""故障"")]
     * Fault = 3,
     * [Description(""正常"")]
     * Normal = 4,
     * [Description(""预警"")]
     * EarlyWarning = 5,
     * [Description(""跳机"")]
     * TripOff = 6,
     * [Description(""待机"")]
     * StandBy = 7,"
     */
    @ExcelIgnore
    private String dCPower;//直流功率
    @ExcelProperty(value = "直流功率")
    private String dcpowerOfExcel;

    @ExcelIgnore
    private String dCVoltage;//直流电压
    @ExcelProperty(value = "直流电压")
    private String dcvoltageOfExcel;

    @ExcelIgnore
    private String dCCurrent;//直流电流
    @ExcelProperty(value = "直流电流")
    private String dccurrentOfExcel;

    @ExcelIgnore
    private String dCRadiatorTemp;//直流散热器温度
    @ExcelProperty(value = "直流散热器温度")
    private String dcradiatorTempOfExcel;

    @ExcelProperty(value = "母线电压正")
    private String mainlineVolPos;//母线电压正
    @ExcelProperty(value = "母线电压负")
    private String mainlineVolNeg;//母线电压负
    @ExcelIgnore
    private Integer BmsWorkStatus;//BMS工作状态 -1:断开 0:充满 1:放空 2:告警 3:故障 4:正常 5:预警 6:跳机 7:待机
    @ExcelProperty(value = "BMS工作状态",converter = BamsRunningStateConverter.class)
    private Integer bmsWorkStatusOfExcel;


    /**
     * BClusterVol	String	 电池簇电压	默认值是浮点数-9999f
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
     * CAVoltage	String	 CA相线电压	默认值是浮点数-9999f
     */
    @ExcelIgnore
    private String bClusterVol;//电池簇电压
    @ExcelProperty(value = "电池簇电压")
    private String bclusterVolOfExcel;

    @ExcelIgnore
    private String bClusterCur;//电池簇电流
    @ExcelProperty(value = "电池簇电流")
    private String bclusterCurOfExcel;

    @ExcelProperty(value = "电池簇可充电量")
    private String groupRechargeableQuantity;//电池簇可充电量
    @ExcelProperty(value = "电池簇SOC")
    private String soc;//电池簇SOC
    @ExcelProperty(value = "电池簇可放电量")
    private String groupRedischargeableQuantity;//电池簇可放电量
    @ExcelProperty(value = "单体最低电池电压")
    private String minVoltage;//单体最低电池电压
    @ExcelProperty(value = "单体最高电池电压")
    private String maxVoltage;//单体最高电池电压
    @ExcelProperty(value = "电池簇最高SOC")
    private String maxSoc;//电池簇最高SOC
    @ExcelProperty(value = "电池簇最低SOC")
    private String minSoc;//电池簇最低SOC
    @ExcelProperty(value = "单体最低电池温度")
    private String minTemp;//单体最低电池温度
    @ExcelProperty(value = "单体最高电池温度")
    private String maxTemp;//单体最高电池温度

    @ExcelIgnore
    private String aVoltage;//A相相电压
    @ExcelProperty(value = "A相相电压")
    private String avoltageOfExcel;

    @ExcelIgnore
    private String bVoltage;//B相相电压
    @ExcelProperty(value = "B相相电压")
    private String bvoltageOfExcel;

    @ExcelIgnore
    private String cVoltage;//C相相电压
    @ExcelProperty(value = "C相相电压")
    private String cvoltageOfExcel;

    @ExcelIgnore
    private String aBVoltage;//AB相线电压
    @ExcelProperty(value = "AB相线电压")
    private String abvoltageOfExcel;

    @ExcelIgnore
    private String bCVoltage;//BC相线电压
    @ExcelProperty(value = "BC相线电压")
    private String bcvoltageOfExcel;

    @ExcelIgnore
    private String cAVoltage;//CA相线电压
    @ExcelProperty(value = "CA相线电压")
    private String cavoltageOfExcel;
    /**
     * ARate	String	 A相电网频率	默认值是浮点数-9999f
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
     * CPower	String	 C相有功功率	默认值是浮点数-9999f
     */
    @ExcelIgnore
    private String aRate;//A相电网频率
    @ExcelProperty(value = "A相电网频率")
    private String arateOfExcel;

    @ExcelIgnore
    private String bRate;//B相电网频率
    @ExcelProperty(value = "B相电网频率")
    private String brateOfExcel;

    @ExcelIgnore
    private String cRate;//C相电网频率
    @ExcelProperty(value = "C相电网频率")
    private String crateOfExcel;

    @ExcelIgnore
    private String aFactor;//A相功率因数
    @ExcelProperty(value = "A相功率因数")
    private String afactorOfExcel;

    @ExcelIgnore
    private String bFactor;//B相功率因数
    @ExcelProperty(value = "B相功率因数")
    private String bfactorOfExcel;

    @ExcelIgnore
    private String cFactor;//C相功率因数
    @ExcelProperty(value = "C相功率因数")
    private String cfactorOfExcel;

    @ExcelIgnore
    private String aCurrent;//A相输出电流
    @ExcelProperty(value = "A相输出电流")
    private String acurrentOfExcel;

    @ExcelIgnore
    private String bCurrent;//B相输出电流
    @ExcelProperty(value = "B相输出电流")
    private String bcurrentOfExcel;

    @ExcelIgnore
    private String cCurrent;//C相输出电流
    @ExcelProperty(value = "C相输出电流")
    private String ccurentOfExcel;

    @ExcelIgnore
    private String aPower;//A相有功功率
    @ExcelProperty(value = "A相有功功率")
    private String apowerOfExcel;

    @ExcelIgnore
    private String bPower;//B相有功功率
    @ExcelProperty(value = "B相有功功率")
    private String bpowerOfExcel;

    @ExcelIgnore
    private String cPower;//C相有功功率
    @ExcelProperty(value = "C相有功功率")
    private String cpowerOfExcel;

    @ExcelIgnore
    private String aReactivePower;//A相无功功率
    @ExcelProperty(value = "A相无功功率")
    private String areactivePowerOfExcel;

    @ExcelIgnore
    private String bReactivePower;//B相无功功率
    @ExcelProperty(value = "B相无功功率")
    private String breactivePowerOfExcel;

    @ExcelIgnore
    private String cReactivePower;//C相无功功率
    @ExcelProperty(value = "C相无功功率")
    private String creactivePowerOfExcel;

    @ExcelIgnore
    private String aApparentPower;//A相视在功率
    @ExcelProperty(value = "A相视在功率")
    private String aapparentPowerOfExcel;

    @ExcelIgnore
    private String bApparentPower;//B相视在功率
    @ExcelProperty(value = "B相视在功率")
    private String bapparentPowerOfExcel;

    @ExcelIgnore
    private String cApparentPower;//C相视在功率
    @ExcelProperty(value = "C相视在功率")
    private String capparentPowerOfExcel;

    @ExcelProperty(value = "三相总有功功率")
    private String sumPower;//三相总有功功率
    @ExcelProperty(value = "三相总无功功率")
    private String sumRelativePower;//三相总无功功率
    @ExcelProperty(value = "三相总视在功率")
    private String sumApparentPower;//三相总视在功率
    @ExcelProperty(value = "三相总功率因数")
    private String sumPowerFactor;//三相总功率因数
    @ExcelIgnore
    private String iNVMainlineVolPos;//INV正母线电压
    @ExcelProperty(value = "INV正母线电压")
    private String invMainlineVolPosOfExcel;

    @ExcelIgnore
    private String iNVMainlineVolNeg;//INV负母线电压
    @ExcelProperty(value = "INV负母线电压")
    private String invMainlineVolNegOfExcel;

    @ExcelProperty(value = "逆变侧散热器温度")
    private String pcsRadiatorTemp;//逆变侧散热器温度
    @ExcelProperty(value = "有功功率期望")
    private String desiredPower;//有功功率期望
    @ExcelProperty(value = "无功功率期望")
    private String desiredReactivePower;//无功功率期望
    @ExcelProperty(value = "最大允许充电功率")
    private String maxChargePower;//最大允许充电功率
    @ExcelProperty(value = "最大允许放电功率")
    private String maxDischargePower;//最大允许放电功率

    /**
     * AReactivePower	String	 A相无功功率	默认值是浮点数-9999f
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
    public String getDcpowerOfExcel() {
        return dCPower;
    }

    public String getDcvoltageOfExcel() {
        return dCVoltage;
    }

    public String getDccurrentOfExcel() {
        return dCCurrent;
    }

    public String getDcradiatorTempOfExcel() {
        return dCRadiatorTemp;
    }

    public Integer getBmsWorkStatusOfExcel() {
        return BmsWorkStatus;
    }

    public String getBclusterVolOfExcel() {
        return bClusterVol;
    }

    public String getBclusterCurOfExcel() {
        return bClusterCur;
    }

    public String getAvoltageOfExcel() {
        return aVoltage;
    }

    public String getBvoltageOfExcel() {
        return bVoltage;
    }

    public String getCvoltageOfExcel() {
        return cVoltage;
    }

    public String getAbvoltageOfExcel() {
        return aBVoltage;
    }

    public String getBcvoltageOfExcel() {
        return bCVoltage;
    }

    public String getCavoltageOfExcel() {
        return cAVoltage;
    }

    public String getArateOfExcel() {
        return aRate;
    }

    public String getBrateOfExcel() {
        return bRate;
    }

    public String getCrateOfExcel() {
        return cRate;
    }

    public String getAfactorOfExcel() {
        return aFactor;
    }

    public String getBfactorOfExcel() {
        return bFactor;
    }

    public String getCfactorOfExcel() {
        return cFactor;
    }

    public String getAcurrentOfExcel() {
        return aCurrent;
    }

    public String getBcurrentOfExcel() {
        return bCurrent;
    }

    public String getCcurentOfExcel() {
        return cCurrent;
    }

    public String getApowerOfExcel() {
        return aPower;
    }

    public String getBpowerOfExcel() {
        return bPower;
    }

    public String getCpowerOfExcel() {
        return cPower;
    }

    public String getAreactivePowerOfExcel() {
        return aReactivePower;
    }

    public String getBreactivePowerOfExcel() {
        return bReactivePower;
    }

    public String getCreactivePowerOfExcel() {
        return cReactivePower;
    }

    public String getAapparentPowerOfExcel() {
        return aApparentPower;
    }

    public String getBapparentPowerOfExcel() {
        return bApparentPower;
    }

    public String getCapparentPowerOfExcel() {
        return cApparentPower;
    }

    public String getInvMainlineVolPosOfExcel() {
        return iNVMainlineVolPos;
    }

    public String getInvMainlineVolNegOfExcel() {
        return iNVMainlineVolNeg;
    }
}
