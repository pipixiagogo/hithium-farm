package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class BcuDataVolTemVo {

    //温度列表
    private Map<String, Integer> tempMap;
    //单体电压
    private Map<String,Integer> volMap;

    private String deviceName;
    private Integer equipmentId;//设备ID
    private String name;//设备名称
    private Date generationDataTime;

    //0-正常，1-异常
    //默认值异常
    private Integer equipChannelStatus;//前置机连接的设备的通道状态
    // 默认值-1即断开
    //     *         [Description(""断开"")]
    //     *         Disconnected = -1,
    //     *         [Description(""充满"")]
    //     *         FullyCharged = 0,
    //     *         [Description(""放空"")]
    //     *         EmptyDischarged = 1,
    //     *         [Description(""告警"")]
    //     *         Warning = 2,
    //     *         [Description(""故障"")]
    //     *         Fault = 3,
    //     *         [Description(""正常"")]
    //     *         Normal = 4,
    //     *         [Description(""预警"")]
    //     *         EarlyWarning = 5,
    //     *         [Description(""跳机"")]
    //     *         TripOff = 6,
    //     *         [Description(""待机"")]
    //     *         StandBy = 7,"
    private Integer groupRunStatus;
    /**
     * [Description(""未定义"")] Undefined = 0,
     * *         [Description(""开路"")] OpenWay = 1,
     * *         [Description(""搁置"")] Abeyance=2,
     * *         [Description(""充电"")] Charge=3,
     * *         [Description(""放电"")] DisCharge=4
     */
    private Integer groupChargeDischargeStatus;
    /**
     * GroupBatteryCount	ushort	电池簇管辖电池节数
     * GroupTemperatureCount	ushort	电池簇管辖温度节数
     * GroupPackCount	ushort	电池簇管辖PACK数
     * MaxIndexVoltage	ushort	单体电压最大节号
     * MinIndexVoltage	ushort	单体电压最小节号
     * MaxIndexTemperature	ushort	单体温度最大节号
     * MinIndexTemperature	ushort	单体温度最小节号
     * MaxIndexResistance	ushort	单体内阻最大节号
     * MinIndexResistance	ushort	单体内阻最小节号
     */
    private Short groupBatteryCount;//电池簇管辖电池节数
    private Short groupTemperatureCount;//电池簇管辖温度节数
    private Short groupPackCount;//电池簇管辖PACK数
    private Short maxIndexVoltage;//单体电压最大节号
    private Short minIndexVoltage;//单体电压最小节号
    private Short maxIndexTemperature;//单体温度最大节号
    private Short minIndexTemperature;//单体温度最小节号
    private Short maxIndexResistance;//单体内阻最大节号
    private Short minIndexResistance;//单体内阻最小节号
    /**
     * MaxIndexSoc	ushort	单体SOC最大节号
     * MinIndexSoc	ushort	 单体SOC最小节号
     * MaxIndexSoh	ushort	 单体SOH最大节号
     * MinIndexSoh	ushort	 单体SOH最小节号
     * DiInput1	bool	 总正直流接触器反馈状态
     * DiInput2	bool	 总负直流接触器反馈状态
     * DiInput3	bool	 消防干接点闭合告警
     * DiInput4	bool	 水浸干接点
     * GroupVoltage	String	 簇端电压	默认值是浮点数-9999f
     * GroupCurrent	String	 簇电流	默认值是浮点数-9999f
     * GroupSoc	String	 簇SOC	默认值是浮点数-9999f
     * GroupSoh	String	 簇SOH	默认值是浮点数-9999f
     * EnvTemperature	String	 环境温度	默认值是浮点数-9999f
     * GroupAnodeInsulateResistance	String	 簇正极绝缘电阻	默认值是浮点数-9999f
     * GroupCathodeInsulateResistance	String	 簇负极绝缘电阻	默认值是浮点数-9999f
     * GroupRechargeableQuantity	String	 簇可充电量	默认值是浮点数-9999f
     * GroupRedischargeableQuantity	String	 簇可放电量	默认值是浮点数-9999f
     * GroupSingleChargeQuantity	String	 簇单次充电电量	默认值是浮点数-9999f
     * GroupSingleDischargeQuantity	String	 簇单次放电电量	默认值是浮点数-9999f
     */
    private Short maxIndexSoc;//单体SOC最大节号
    private Short minIndexSoc;//单体SOC最小节号
    private Short maxIndexSoh;//单体SOH最大节号
    private Short minIndexSoh;//单体SOH最小节号

    private Boolean diInput1;//总正直流接触器反馈状态

    private Boolean diInput2;//总负直流接触器反馈状态

    private Boolean diInput3;//消防干接点闭合告警

    private Boolean diInput4;//水浸干接点
    private String groupVoltage;//簇端电压
    private String groupCurrent;//簇电流

    private String groupSoc;//簇SOC

    private String groupSoh;//簇SOH
    private String envTemperature;//环境温度

    private String groupAnodeInsulateResistance;//簇正极绝缘电阻

    private String groupCathodeInsulateResistance;//簇负极绝缘电阻

    private String groupRechargeableQuantity;//簇可充电量

    private String groupRedischargeableQuantity;//簇可放电量

    private String groupSingleChargeQuantity;//簇单次充电电量

    private String groupSingleDischargeQuantity;//簇单次放电电量
    /**
     * GroupAccuChargeQuantity	String	 簇充电电量累加值	默认值是浮点数-9999f
     * GroupAccuDischargeQuantity	String	 簇放电电量累加值	默认值是浮点数-9999f
     * MonitorTemperature1	String	 监测温度1	默认值是浮点数-9999f
     * MonitorTemperature2	String	 监测温度2	默认值是浮点数-9999f
     * MonitorTemperature3	String	 监测温度3	默认值是浮点数-9999f
     * AvgSingleVoltage	String	 单体电压平均值	默认值是浮点数-9999f
     * MaxSingleVoltage	String	 单体电压最大值	默认值是浮点数-9999f
     * MinSingleVoltage	String	 单体电压最小值	默认值是浮点数-9999f
     * CellVoltageDiff	String	 单体压差	默认值是浮点数-9999f
     * AvgSingleTemperature	String	 单体温度平均值	默认值是浮点数-9999f
     * MaxSingleTemperature	String	 单体温度最大值	默认值是浮点数-9999f
     * MinSingleTemperature	String	 单体温度最小值	默认值是浮点数-9999f
     * CellTemperatureDiff	String	 单体温差	默认值是浮点数-9999f
     * AvgSingleResistance	String	 单体内阻平均值	默认值是浮点数-9999f
     * MaxSingleResistance	String	 单体内阻最大值	默认值是浮点数-9999f
     * MinSingleResistance	String	 单体内阻最小值	默认值是浮点数-9999f
     */

    private String groupAccuChargeQuantity;//簇充电电量累加值

    private String groupAccuDischargeQuantity;//簇放电电量累加值

    private String monitorTemperature1;//监测温度1

    private String monitorTemperature2;//监测温度1

    private String monitorTemperature3;//监测温度1
    private String avgSingleVoltage;//单体电压平均值
    private String maxSingleVoltage;//单体电压最大值
    private String minSingleVoltage;//单体电压最小值
    private String cellVoltageDiff;//单体压差
    private String avgSingleTemperature;//单体温度平均值
    private String maxSingleTemperature;//单体温度最大值
    private String minSingleTemperature;//单体温度最小值
    private String cellTemperatureDiff;//单体温差

    private String avgSingleResistance;//单体内阻平均值

    private String maxSingleResistance;//单体内阻最大值

    private String minSingleResistance;//单体内阻最小值
    /**
     * AvgSingleSoc	String	 单体SOC平均值	默认值是浮点数-9999f
     * MaxSingleSoc	String	 单体SOC最大值	默认值是浮点数-9999f
     * MinSingleSoc	String	 单体SOC最小值	默认值是浮点数-9999f
     * AvgSingleSoh	String	 单体SOH平均值	默认值是浮点数-9999f
     * MaxSingleSoh	String	 单体SOH最大值	默认值是浮点数-9999f
     * MinSingleSoh	String	 单体SOH最小值	默认值是浮点数-9999f
     * MaxGroupAllowChargePower	String	 簇当前允许最大充电功率	默认值是浮点数-9999f
     * MaxGroupAllowDischargePower	String	 簇当前允许最大放电功率	默认值是浮点数-9999f
     * MaxGroupAllowChargeCurrent	String	 簇当前允许最大充电电流	默认值是浮点数-9999f
     * MaxGroupAllowDischargeCurrent	String	 簇当前允许最大放电电流	默认值是浮点数-9999f
     * PreVoltHighLimitAlarm	bool	 簇端电压上限预警
     * PreVoltLowLimitAlarm	bool	 簇端电压下限预警
     * PreChargeCurrHighLimitAlarm	bool	 充电电流上限预警
     * PreDischargeCurrHighLimitAlarm	bool	 放电电流上限预警
     * PreSingleVoltHighLimitAlarm	bool	 单体电压上限预警
     * PreSingleVoltLowLimitAlarm	bool	 单体电压下限预警
     * PreChargeTempHighLimitAlarm	bool	 充电温度上限预警
     * PreChargeTempLowLimitAlarm	bool	 充电温度下限预警
     */

    private String avgSingleSoc;//单体SOC平均值

    private String maxSingleSoc;//单体SOC最大值

    private String minSingleSoc;//单体SOC最小值

    private String avgSingleSoh;//单体SOH平均值

    private String maxSingleSoh;//单体SOH最大值

    private String minSingleSoh;//单体SOH最小值

    private String maxGroupAllowChargePower;//簇当前允许最大充电功率

    private String maxGroupAllowDischargePower;//簇当前允许最大放电功率

    private String maxGroupAllowChargeCurrent;//簇当前允许最大充电电流

    private String maxGroupAllowDischargeCurrent;//簇当前允许最大放电电流
    private Boolean preVoltHighLimitAlarm;//簇端电压上限预警
    private Boolean preVoltLowLimitAlarm;//簇端电压下限预警
    private Boolean preChargeCurrHighLimitAlarm;//充电电流上限预警
    private Boolean preDischargeCurrHighLimitAlarm;//放电电流上限预警
    private Boolean preSingleVoltHighLimitAlarm;//单体电压上限预警
    private Boolean preSingleVoltLowLimitAlarm;//单体电压下限预警
    private Boolean preChargeTempHighLimitAlarm;//充电温度上限预警
    private Boolean preChargeTempLowLimitAlarm;//充电温度下限预警

    /**
     * PreDischargeTempHighLimitAlarm	bool	 放电温度上限预警
     * PreDischargeTempLowLimitAlarm	bool	 放电温度下限预警
     * PreSingleTempHighLimitAlarm	bool	 单体温度上限预警
     * PreSingleTempLowLimitAlarm	bool	 单体温度下限预警
     * PreEnvTempHighLimitAlarm	bool	 环境温度上限预警
     * PreEnvTempLowLimitAlarm	bool	 环境温度下限预警
     * PrePowerLineTempHighLimitAlarm	bool	 动力线温度上限预警
     * PreGroupSocHighLimitAlarm	bool	 簇SOC上限预警
     * PreGroupSocLowLimitAlarm	bool	 簇SOC下限预警
     * PreAnodeInsulateResistanceLowLimitAlarm	bool	 正极绝缘内阻下限预警
     * PreCathodeInsulateResistanceLowLimitAlarm	bool	 负极绝缘内阻下限预警
     * PrePackVoltDiffHighLimitAlarm	bool	 箱压差上限预警
     * PrePackTempDiffHighLimitAlarm	bool	 箱温差上限预警
     * VoltHighLimitAlarm	bool	 簇端电压上限告警
     * VoltLowLimitAlarm	bool	 簇端电压下限告警
     */
    private Boolean preDischargeTempHighLimitAlarm;//放电温度上限预警
    private Boolean preDischargeTempLowLimitAlarm;//放电温度下限预警
    private Boolean preSingleTempHighLimitAlarm;//单体温度上限预警
    private Boolean preSingleTempLowLimitAlarm;//单体温度下限预警
    private Boolean preEnvTempHighLimitAlarm;//环境温度上限预警
    private Boolean preEnvTempLowLimitAlarm;//环境温度下限预警
    private Boolean prePowerLineTempHighLimitAlarm;//动力线温度上限预警
    private Boolean preGroupSocHighLimitAlarm;//簇SOC上限预警
    private Boolean preGroupSocLowLimitAlarm;//簇SOC下限预警
    private Boolean preAnodeInsulateResistanceLowLimitAlarm;//正极绝缘内阻下限预警
    private Boolean preCathodeInsulateResistanceLowLimitAlarm;//负极绝缘内阻下限预警
    private Boolean prePackVoltDiffHighLimitAlarm;//箱压差上限预警
    private Boolean prePackTempDiffHighLimitAlarm;//箱温差上限预警
    private Boolean voltHighLimitAlarm;//簇端电压上限告警
    private Boolean voltLowLimitAlarm;//簇端电压下限告警

    /**
     * ChargeCurrHighLimitAlarm	bool	 充电电流上限告警
     * DischargeCurrHighLimitAlarm	bool	 放电电流上限告警
     * SingleVoltHighLimitAlarm	bool	 单体电压上限告警
     * SingleVoltLowLimitAlarm	bool	 单体电压下限告警
     * ChargeTempHighLimitAlarm	bool	 充电温度上限告警
     * ChargeTempLowLimitAlarm	bool	 充电温度下限告警
     * DischargeTempHighLimitAlarm	bool	 放电温度上限告警
     * DischargeTempLowLimitAlarm	bool	 放电温度下限告警
     * SingleTempHighLimitAlarm	bool	 单体温度上限告警
     * SingleTempLowLimitAlarm	bool	 单体温度下限告警
     * EnvTempHighLimitAlarm	bool	 环境温度上限告警
     * EnvTempLowLimitAlarm	bool	 环境温度下限告警
     * PowerLineTempHighLimitAlarm	bool	 动力线温度上限告警
     * GroupSocHighLimitAlarm	bool	 电池簇充放电状态
     * GroupSocLowLimitAlarm	bool	 簇SOC下限告警
     * AnodeInsulateResistanceLowLimitAlarm	bool	 正极绝缘内阻下限告警
     * CathodeInsulateResistanceLowLimitAlarm	bool	 负极绝缘内阻下限告警
     * PackVoltDiffHighLimitAlarm	bool	 箱压差上限告警
     * PackTempDiffHighLimitAlarm	bool	 箱温差上限告警
     * VoltHighLimitProtect	bool	 簇端电压上限保护
     * VoltLowLimitProtect	bool	 簇端电压下限保护
     */
    private Boolean chargeCurrHighLimitAlarm;//充电电流上限告警
    private Boolean dischargeCurrHighLimitAlarm;//放电电流上限告警
    private Boolean singleVoltHighLimitAlarm;//单体电压上限告警
    private Boolean singleVoltLowLimitAlarm;//单体电压下限告警
    private Boolean chargeTempHighLimitAlarm;//充电温度上限告警
    private Boolean chargeTempLowLimitAlarm;//充电温度下限告警
    private Boolean dischargeTempHighLimitAlarm;//放电温度上限告警
    private Boolean dischargeTempLowLimitAlarm;//放电温度下限告警
    private Boolean singleTempHighLimitAlarm;//单体温度上限告警
    private Boolean singleTempLowLimitAlarm;//单体温度下限告警
    private Boolean envTempHighLimitAlarm;//环境温度上限告警
    private Boolean envTempLowLimitAlarm;//环境温度下限告警
    private Boolean powerLineTempHighLimitAlarm;//动力线温度上限告警
    private Boolean groupSocHighLimitAlarm;//电池簇充放电状态
    private Boolean groupSocLowLimitAlarm;//簇SOC下限告警
    private Boolean anodeInsulateResistanceLowLimitAlarm;//正极绝缘内阻下限告警
    private Boolean cathodeInsulateResistanceLowLimitAlarm;//负极绝缘内阻下限告警
    private Boolean packVoltDiffHighLimitAlarm;//箱压差上限告警
    private Boolean packTempDiffHighLimitAlarm;//箱温差上限告警
    private Boolean voltHighLimitProtect;//簇端电压上限保护
    private Boolean voltLowLimitProtect;//簇端电压下限保护

    /**
     * ChargeCurrHighLimitProtect	bool	 充电电流上限保护
     * DischargeCurrHighLimitProtect	bool	 放电电流上限保护
     * SingleVoltHighLimitProtect	bool	 单体电压上限保护
     * SingleVoltLowLimitProtect	bool	 单体电压下限保护
     * ChargeTempHighLimitProtect	bool	 充电温度上限保护
     * ChargeTempLowLimitProtect	bool	 充电温度下限保护
     * DischargeTempHighLimitProtect	bool	 放电温度上限保护
     * DischargeTempLowLimitProtect	bool	 放电温度下限保护
     * SingleTempHighLimitProtect	bool	 单体温度上限保护
     * SingleTempLowLimitProtect	bool	 单体温度下限保护
     * <p>
     * EnvTempHighLimitProtect	bool	 环境温度上限保护
     * EnvTempLowLimitProtect	bool	 环境温度下限保护
     * PowerLineTempHighLimitProtect	bool	 动力线温度上限保护
     * GroupSocHighLimitProtect	bool	 簇SOC上限保护
     * GroupSocLowLimitProtect	bool	 簇SOC下限保护
     * AnodeInsulateResistanceLowLimitProtect	bool	 正极绝缘内阻下限保护
     * CathodeInsulateResistanceLowLimitProtect	bool	 负极绝缘内阻下限保护
     */

    private Boolean chargeCurrHighLimitProtect;//充电电流上限保护

    private Boolean dischargeCurrHighLimitProtect;//放电电流上限保护

    private Boolean singleVoltHighLimitProtect;//单体电压上限保护

    private Boolean singleVoltLowLimitProtect;//单体电压下限保护

    private Boolean chargeTempHighLimitProtect;//充电温度上限保护

    private Boolean chargeTempLowLimitProtect;//充电温度下限保护

    private Boolean dischargeTempHighLimitProtect;//放电温度上限保护

    private Boolean dischargeTempLowLimitProtect;//放电温度下限保护

    private Boolean singleTempHighLimitProtect;//单体温度上限保护

    private Boolean singleTempLowLimitProtect;//单体温度下限保护

    private Boolean envTempHighLimitProtect;//环境温度上限保护

    private Boolean envTempLowLimitProtect;//环境温度下限保护

    private Boolean powerLineTempHighLimitProtect;//动力线温度上限保护

    private Boolean groupSocHighLimitProtect;//簇SOC上限保护

    private Boolean groupSocLowLimitProtect;//簇SOC下限保护

    private Boolean anodeInsulateResistanceLowLimitProtect;//正极绝缘内阻下限保护

    private Boolean cathodeInsulateResistanceLowLimitProtect;//负极绝缘内阻下限保护

    /**
     * PackVoltDiffHighLimitProtect	bool	 箱压差上限保护
     * PackTempDiffHighLimitProtect	bool	 箱温差上限保护
     * BattTempRiseFast	bool	 电池温度温升过快
     * PowerLineTempRiseFast	bool	 动力线温度温升过快
     * BmuCommFault	bool	 BMU通讯故障
     * BcmuCommFault	bool	 BCMU通讯故障
     * SingleVoltAcqLineFault	bool	 单体电压采集线故障
     * TotalVoltAcqLineFault	bool	 总压采集线故障
     * CurrentAcqLineFault	bool	 电流采集线故障
     * TempAcqOffline	bool	 温度采集线断线
     * TempAcqShortCircuit	bool	 温度采集线短路
     * BmsDeviceFault	bool	 BMS设备故障
     * SingleVoltDisable	bool	 单体电压无效
     * SingleTempDisable	bool	 单体温度无效
     */

    private Boolean packVoltDiffHighLimitProtect;//箱压差上限保护

    private Boolean packTempDiffHighLimitProtect;//箱温差上限保护

    private Boolean battTempRiseFast;//电池温度温升过快

    private Boolean powerLineTempRiseFast;//动力线温度温升过快

    private Boolean bmuCommFault;//BMU通讯故障

    private Boolean bcmuCommFault;//BCMU通讯故障

    private Boolean singleVoltAcqLineFault;//单体电压采集线故障

    private Boolean totalVoltAcqLineFault;//总压采集线故障

    private Boolean currentAcqLineFault;//电流采集线故障

    private Boolean tempAcqOffline;//温度采集线断线

    private Boolean tempAcqShortCircuit;//温度采集线短路

    private Boolean bmsDeviceFault;//BMS设备故障

    private Boolean singleVoltDisable;//单体电压无效

    private Boolean singleTempDisable;//单体温度无效

    private Boolean groupVoltDisable;//簇端电压无效

    private Boolean groupCurrentException;//簇电流异常

    private Boolean fireAlarm;//消防报警

    private Boolean waterLoggingAlarm;//水浸报警

    private Boolean cacheAlarm;//缓存告警待获取

    private Integer preAlarmStatus;//电池簇状态(预警)

    private Integer alarmStatus;//电池簇状态(告警)

    private Integer protectStatus;//电池簇状态(保护)

    private Integer faultStatus;//电池簇状态(故障)
}
