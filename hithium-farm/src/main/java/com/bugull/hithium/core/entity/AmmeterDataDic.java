package com.bugull.hithium.core.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 电表
 */
@Data
@Entity
@EnsureIndex("{deviceName:1,equipmentId:1,generationDataTime:-1}")
public class AmmeterDataDic extends SimpleEntity {

   
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道	"0-正常，1-异常
     * 默认异常"
     * Pt	float	电压变比
     * Ct	float	电流变比
     * CurTotalActPower	float	当前总有功电能
     * CurTotalActTipPower	float	当前总有功尖电能
     * CurTotalActPeakPower	float	当前总有功峰电能
     * CurTotalActFlatPower	float	当前总有功平电能
     * <p>
     * CurTotalActValleyPower	float	当前总有功谷电能
     */
    /**
     * 设备名称
     */
    private String name;
    private Integer equipmentId;
    /**
     * 储能站整体名称
     */
    private String deviceName;
   
    @Ignore
    private String time;
    private Date generationDataTime;
    //设备ID
    //前置机连接的设备的通道	"0-正常，1-异常  默认异常
    private Integer equipChannelStatus;
    //电压变化
    private String pt;
    //电流变化
    private String ct;
    //当前总有功电能
    private String curTotalActPower;
    //当前总有功尖电能
    private String curTotalActTipPower;
    //当前总有功峰电能
    private String curTotalActPeakPower;

    //当前总有功平电能
    private String curTotalActFlatPower;
    //当前总有功谷电能
    private String curTotalActValleyPower;

    //当前正向总有功电能
    private String totalChargeQuantity;
    //当前正向总有功尖电能
   
    private String totalTipChargeQuantity;
    //当前正向总有功峰电能
   
    private String totalPeakChargeQuantity;
    //当前正向总有功平电能
   
    private String totalFlatChargeQuantity;
    //当前正向总有功谷电能
   
    private String totalValleyChargeQuantity;
    //当前反向总有功电能
    private String totalDischargeQuantity;
   
    private String totalTipDischargeQuantity;//当前反向总有功尖电能
   
    private String totalPeakDischargeQuantity;//当前反向总有功峰电能
   
    private String totalFlatDischargeQuantity;//当前反向总有功平电能
   
    private String totalValleyDischargeQuantity;//当前反向总有功谷电能

   
    private String curTotalReactPower;//当前总无功电能
   
    private String curTotalReactTipPower;//当前总无功尖电能
   
    private String curTotalReactPeakPower;//当前总无功峰电能
   
    private String curTotalReactFlatPower;//当前总无功平电能
   
    private String curTotalReactValleyPower;//当前总无功谷电能

   
    private String curPosTotalReactPower;//当前正向总无功电能
   
    private String curPosTotalReactTipPower;//当前正向总无功尖电能
   
    private String curPosTotalReactPeakPower;//当前正向总无功峰电能
   
    private String curPosTotalReactFlatPower;//当前正向总无功平电能
   
    private String curPosTotalReactValleyPower;//当前正向总无功谷电能
   
    private String curNegTotalReactPower;//当前反向总无功电能
   
    private String curNegTotalReactTipPower;//当前反向总无功尖电能

   
    private String curNegTotalReactPeakPower;//当前反向总无功峰电能
   
    private String curNegTotalReactValleyPower;//当前反向总无功平电能
   
    private String curNegTotalReactFlatPower;//当前反向总无功平电能
   
    private String aPhaseVoltage;//A相电压
   
    private String bPhaseVoltage;//B相电压
   
    private String cPhaseVoltage;//C相电压
   
    private String aPhaseCurrent;//A相电流
   
    private String bPhaseCurrent;//B相电流
   
    private String cPhaseCurrent;//C相电流
    /**
     * Frequency	String	频率
     * AbLineVoltage	String	AB线电压
     * BcLineVoltage	String	BC线电压
     * CaLineVoltage	String	CA线电压
     * LossVoltData	ushort	失压数据HB
     * APhaseActivePower	String	A相有功功率
     * BPhaseActivePower	String	B相有功功率
     * CPhaseActivePower	String	C相有功功率
     * TotalActivePower	String	总有功功率
     */
   
    private String frequency;
   
    private String abLineVoltage;//AB线电压
   
    private String bcLineVoltage;//BC线电压
   
    private String caLineVoltage;//CA线电压
   
    private Short lossVoltData;//失压数据HB
   
    private String aPhaseActivePower;//A相有功功率
   
    private String bPhaseActivePower;//B相有功功率
   
    private String cPhaseActivePower;//C相有功功率
   
    private String totalActivePower;//总有功功率
   
    private String aPhaseReactivePower;//A相无功功率
   
    private String bPhaseReactivePower;//B相无功功率
   
    private String cPhaseReactivePower;//C相无功功率
   
    private String totalReactivePower;//总无功功率
   
    private String aPhaseDependPower;//A相视在功率
   
    private String bPhaseDependPower;//B相视在功率
   
    private String cPhaseDependPower;//C相视在功率
   
    private String totalDependPower;//总视在功率
   
    private String aPhasePowerFactor;
   
    private String bPhasePowerFactor;
   
    private String cPhasePowerFactor;
   
    private String totalPowerFactor;//总功率因数

    //"-1 代表 无 0 代表 公网   1 代表 储能箱放电
    private Integer loadMeterElectricityType;//负荷电表用电来源
    /**
     * APhaseReactivePower	String	A相无功功率
     * BPhaseReactivePower	String	B相无功功率
     * CPhaseReactivePower	String	C相无功功率
     * TotalReactivePower	String	总无功功率
     *
     * APhaseDependPower	String	A相视在功率
     * BPhaseDependPower	String	B相视在功率
     * CPhaseDependPower	String	C相视在功率
     * TotalDependPower	String	总视在功率
     * APhasePowerFactor	String	A相功率因数
     * BPhasePowerFactor	String	B相功率因数
     * CPhasePowerFactor	String	C相功率因数
     * TotalPowerFactor	String	总功率因数
     * LoadMeterElectricityType	int	负荷电表用电来源	"-1 代表 无
     * 0 代表 公网
     * 1 代表 储能箱放电
     * "
     */
}
