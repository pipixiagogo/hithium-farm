package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 主机
 */
@Data
@ToString
@Entity
@EnsureIndex("{deviceName:1,equipmentId:1,generationDataTime:1}")
public class PcsCabinetDic extends SimpleEntity {


    private String deviceName;
    private String name;
    @Ignore
    private String time;
    private Integer equipmentId;
    //0-正常，1-异常
    //默认值异常
    private Integer equipChannelStatus;//前置机连接的设备的通道状态
    private Date generationDataTime;
    private Short powerOnOff;//开关机
    private Short faultClear;//故障清除
    /**
     * Time	DateTime	时间
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态
     * PowerOnOff	ushort	开关机
     * FaultClear	ushort	故障清除
     * Ipv4Address	uint	IPV4网络地址
     * SubMask	uint	子网掩码
     * DefaultGatway	uint	默认网关
     * DnsServer	uint	DNS服务器
     * SerialBaudrate	ushort	串口波特率
     * ModbusAddress	ushort	MODBUS地址
     * LocalRemoteCtrl	ushort	本地/远程控制
     * AlarmSoundCtrl	ushort	报警声音控制
     */
    private Integer ipv4Address;//IPV4网络地址
    private Integer subMask;//子网掩码
    private Integer defaultGatway;//默认网关
    private Integer dnsServer;//DNS服务器
    private Short serialBaudrate;//串口波特率
    private Short modbusAddress;//MODBUS地址
    private Short localRemoteCtrl;//本地/远程控制
    private Short alarmSoundCtrl;//报警声音控制
    /** SystemTimeYear	ushort	系统时间-年
     * SystemTimeMonth	ushort	系统时间-月
     * SystemTimeDay	ushort	系统时间-日
     * SystemTimeHour	ushort	系统时间-时
     * SystemTimeMinute	ushort	系统时间-分
     * SystemTimeSecond	ushort	系统时间-秒
     * MacAddress1	ushort	MAC地址1
     * MacAddress2	ushort	MAC地址2
     * MacAddress3	ushort	MAC地址3
     * ActivePower	int	有功功率
     * ReactivePower	int	无功功率
     * StatusWord	ushort	整机状态字1
     * StatusWord2	ushort	整机状态字2
     * IsLocal	bool	本地控制
     * FaultStatus	bool	 故障状态
     * WarningStatus	bool	 告警状态
     * IsRunning	bool	 启停状态
     * IsOffGrid	bool	 并离网状态*/
    private Short systemTimeYear;//系统时间-年
    private Short systemTimeMonth;//系统时间-月
    private Short systemTimeDay;//系统时间-日
    private Short systemTimeHour;//系统时间-时
    private Short systemTimeMinute;//系统时间-分
    private Short systemTimeSecond;//系统时间-秒
    private Short macAddress1;//MAC地址1
    private Short macAddress2;//MAC地址2
    private Short macAddress3;//MAC地址3
    private Integer activePower;//有功功率
    private Integer reactivePower;//无功功率
    private Short statusWord;//整机状态字1
    private Short statusWord2;//整机状态字2
    private Boolean isLocal;//本地控制
    private Boolean faultStatus;//故障状态
    private Boolean warningStatus;//告警状态
    private Boolean isRunning;//启停状态
    private Boolean isOffGrid;//并离网状态

    //0:待机 1:充电 2:放电 3:停电
    private Integer chargeDischargeStatus;//充放电状态
     /** ChargeDischargeStatus	EChargeOrDischarge	 充放电状态
      */
    /** PcsMonitorCanFault	bool	 PCS监控CAN通信故障
     * EpoFault	bool	 EPO故障
     * SpdAlarm	bool	 防雷器告警
     * EmsAndPcsCommAlarm	bool	 EMS-PCS通信告警
     * PmsAndPcsCommFault	bool	 PMS-PCS通信故障
     * FanFault	bool	 风扇故障
     * AuxPowerFault	bool	 辅源故障
     * EepromFault	bool	 EEPROM故障
     * OutPhaseReverse	bool	 输出相序反
     * OverLoadAlarm	bool	 过载告警
     * OverLoadTimeout	bool	 过载超时
     * BypassCircuitWithoutZero	bool	 旁路缺零
     * BypassCircuitException	bool	 旁路异常
     * BypassCircuitPhaseReverse	bool	 旁路相序反*/
     private Boolean pcsMonitorCanFault;//PCS监控CAN通信故障
     private Boolean epoFault;//EPO故障
     private Boolean spdAlarm;//防雷器告警
     private Boolean emsAndPcsCommAlarm;//EMS-PCS通信告警
     private Boolean pmsAndPcsCommFault;//PMS-PCS通信故障
     private Boolean fanFault;//风扇故障
     private Boolean auxPowerFault;//辅源故障
    private Boolean eepromFault;//EEPROM故障
    private Boolean outPhaseReverse;//辅源故障
    private Boolean overLoadAlarm;//过载告警
    private Boolean overLoadTimeout;//过载超时
    private Boolean bypassCircuitWithoutZero;//旁路缺零
    private Boolean bypassCircuitException;//旁路异常
    private Boolean bypassCircuitPhaseReverse;//旁路相序反

    /** OffGridSoftStartFail	bool	 离网软起失败
     * AcOverLoadProtect	bool	 交流过流保护
     * SysLocalConflict	bool	 系统本地冲突
     * InverterVoltageException	bool	 逆变电压异常
     * InverterShortCircuitFault	bool	 逆变短路故障
     * MainControlCommFault	bool	 PMS-PCS主控通信故障
     * OffGridAcPortVoltageException	bool	 离网交流端口电压异常
     * BypassCircuitABVol	String	 旁路AB线电压
     * BypassCircuitBCVol	String	 旁路BC线电压
     * BypassCircuitCAVol	String	 旁路CA线电压
     * BypassCircuitAVol	String	 旁路A线电压
     * BypassCircuitBVol	String	 旁路B线电压
     * BypassCircuitCVol	String	 旁路C线电压
     * BypassCircuitARate	String	 旁路A相频率
     * BypassCircuitBRate	String	 旁路B相频率
     * BypassCircuitCRate	String	 旁路C相频率*/
    private Boolean offGridSoftStartFail;//离网软起失败
    private Boolean acOverLoadProtect;//交流过流保护
    private Boolean sysLocalConflict;//系统本地冲突
    private Boolean inverterVoltageException;//逆变电压异常
    private Boolean inverterShortCircuitFault;//逆变短路故障
    private Boolean mainControlCommFault;//PMS-PCS主控通信故障
    private Boolean offGridAcPortVoltageException;//离网交流端口电压异常
    private String bypassCircuitABVol;//旁路AB线电压
    private String bypassCircuitBCVol;//旁路BC线电压
    private String bypassCircuitCAVol;//旁路CA线电压
    private String bypassCircuitAVol;//旁路A线电压
    private String bypassCircuitBVol;//旁路B线电压
    private String bypassCircuitCVol;//旁路C线电压
    private String bypassCircuitARate;//旁路A相频率
    private String bypassCircuitBRate;//旁路B相频率
    private String bypassCircuitCRate;//旁路C相频率
    /** ABVol	String	 AB线电压
     * BCVol	String	 BC线电压
     * CAVol	String	 CA线电压
     * AVol	String	 A线电压
     * BVol	String	 B线电压
     * CVol	String	 C线电压
     * ARate	String	 A相频率
     * BRate	String	 B相频率
     * CRate	String	 C相频率
     * APF	String	 A相功率因数
     * BPF	String	 B相功率因数
     * CPF	String	 C相功率因数*/
    private String aBVol;//AB线电压
    private String bCVol;//BC线电压
    private String cAVol;//CA线电压
    private String aVol;//A线电压
    private String bVol;//B线电压
    private String cVol;//C线电压
    private String aRate;//A相频率
    private String bRate;//B相频率
    private String cRate;//C相频率
    private String aPF;//A相功率因数
    private String bPF;//B相功率因数
    private String cPF;//C相功率因数
    /** ACurrent	String	 A相电流
     * BCurrent	String	 B相电流
     * CCurrent	String	 C相电流
     * APower	String	 A相有功功率
     * BPower	String	 B相有功功率
     * CPower	String	 C相有功功率
     * AReactivePower	String	 A相无功功率
     * BReactivePower	String	 B相无功功率
     * CReactivePower	String	 C相无功功率
     * AApparentPower	String	 A相视在功率
     * BApparentPower	String	 B相视在功率
     * CApparentPower	String	 C相视在功率
     * SumPower	String	 三相总有功功率
     * SumReactivePower	String	 三相总无功功率
     * SumApparentPower	String	 三相总视在功率
     * SumPowerFactor	String	 三相总功率因数*/
    private String aCurrent;//A相电流
    private String bCurrent;//B相电流
    private String cCurrent;//C相电流
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
    private String sumReactivePower;//三相总无功功率
    private String sumApparentPower;//三相总视在功率
    private String sumPowerFactor;//三相总功率因数

    private String sumCharge;//交流总充电电量
    private String sumDischarge;//交流总放电电量
    private String daySumCharge;//交流日充电电量
    private String daySumDischarge;//交流日放电电量
    private String maxChargePower;//最大允许充电功率;
    private String maxDischargePower;//最大允许放电功率
    private String desiredPower;//有功功率期望值
    private String desiredReactivePower;//无功功率期望值
     /** SumCharge	String	 交流总充电电量
     * SumDischarge	String	 交流总放电电量
     * DaySumCharge	String	 交流日充电电量
     * DaySumDischarge	String	 交流日放电电量
     * MaxChargePower	String	 最大允许充电功率
     * MaxDischargePower	String	 最大允许放电功率
     * DesiredPower	String	 有功功率期望值
     * DesiredReactivePower	String	 无功功率期望值
     */
}
