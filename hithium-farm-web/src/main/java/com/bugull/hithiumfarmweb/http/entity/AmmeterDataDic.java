package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.EquipStatusConverter;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 电表
 */
@Data
@Entity
public class AmmeterDataDic implements BuguEntity {


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
    @ExcelIgnore
    private String id;
    @ExcelProperty(value = "设备名称")
    private String name;
    @ExcelIgnore
    private Integer equipmentId;
    /**
     * 储能站整体名称
     */
    @ExcelIgnore
    private String deviceName;

    @Ignore
    @ExcelIgnore
    private String time;
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    //设备ID
    //前置机连接的设备的通道	"0-正常，1-异常  默认异常
    @ExcelProperty(value = "前置机通道状态", converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;
    //电压变化
    @ExcelProperty(value = "电压变化")
    private String pt;
    //电流变化
    @ExcelProperty(value = "电流变化")
    private String ct;
    //当前总有功电能
    @ExcelProperty(value = "当前总有功电能")
    private String curTotalActPower;
    //当前总有功尖电能
    @ExcelProperty(value = "当前总有功尖电能")
    private String curTotalActTipPower;


    //当前总有功峰电能
    @ExcelProperty(value = "当前总有功峰电能")
    private String curTotalActPeakPower;

    //当前总有功平电能
    @ExcelProperty(value = "当前总有功平电能")
    private String curTotalActFlatPower;
    //当前总有功谷电能
    @ExcelProperty(value = "当前总有功谷电能")
    private String curTotalActValleyPower;

    //当前正向总有功电能
    @ExcelProperty(value = "当前正向总有功电能")
    private String totalChargeQuantity;
    //当前正向总有功尖电能
    @ExcelProperty(value = "当前正向总有功尖电能")
    private String totalTipChargeQuantity;
    //当前正向总有功峰电能
    @ExcelProperty(value = "当前正向总有功峰电能")
    private String totalPeakChargeQuantity;
    //当前正向总有功平电能
    @ExcelProperty(value = "当前正向总有功平电能")
    private String totalFlatChargeQuantity;
    //当前正向总有功谷电能
    @ExcelProperty(value = "当前正向总有功谷电能")
    private String totalValleyChargeQuantity;
    //当前反向总有功电能
    @ExcelProperty(value = "当前反向总有功电能")
    private String totalDischargeQuantity;
    @ExcelProperty(value = "当前反向总有功尖电能")
    private String totalTipDischargeQuantity;//当前反向总有功尖电能
    @ExcelProperty(value = "当前反向总有功峰电能")
    private String totalPeakDischargeQuantity;//当前反向总有功峰电能
    @ExcelProperty(value = "当前反向总有功平电能")
    private String totalFlatDischargeQuantity;//当前反向总有功平电能
    @ExcelProperty(value = "当前反向总有功谷电能")
    private String totalValleyDischargeQuantity;//当前反向总有功谷电能

    @ExcelProperty(value = "当前总无功电能")
    private String curTotalReactPower;//当前总无功电能
    @ExcelProperty(value = "当前总无功尖电能")
    private String curTotalReactTipPower;//当前总无功尖电能
    @ExcelProperty(value = "当前总无功峰电能")
    private String curTotalReactPeakPower;//当前总无功峰电能
    @ExcelProperty(value = "当前总无功平电能")
    private String curTotalReactFlatPower;//当前总无功平电能
    @ExcelProperty(value = "当前总无功谷电能")
    private String curTotalReactValleyPower;//当前总无功谷电能

    @ExcelProperty(value = "当前正向总无功电能")
    private String curPosTotalReactPower;//当前正向总无功电能
    @ExcelProperty(value = "当前正向总无功尖电能")
    private String curPosTotalReactTipPower;//当前正向总无功尖电能
    @ExcelProperty(value = "当前正向总无功峰电能")
    private String curPosTotalReactPeakPower;//当前正向总无功峰电能
    @ExcelProperty(value = "当前正向总无功平电能")
    private String curPosTotalReactFlatPower;//当前正向总无功平电能
    @ExcelProperty(value = "当前正向总无功谷电能")
    private String curPosTotalReactValleyPower;//当前正向总无功谷电能
    @ExcelProperty(value = "当前反向总无功电能")
    private String curNegTotalReactPower;//当前反向总无功电能
    @ExcelProperty(value = "当前反向总无功尖电能")
    private String curNegTotalReactTipPower;//当前反向总无功尖电能

    @ExcelProperty(value = "当前反向总无功峰电能")
    private String curNegTotalReactPeakPower;//当前反向总无功峰电能
    @ExcelProperty(value = "当前反向总无功平电能")
    private String curNegTotalReactValleyPower;//当前反向总无功平电能
    @ExcelProperty(value = "当前反向总无功平电能")
    private String curNegTotalReactFlatPower;//当前反向总无功平电能
    @ExcelIgnore
    private String aPhaseVoltage;//A相电压
    @ExcelProperty(value = "A相电压")
    private String aphaseVoltageOfExcel;

    @ExcelIgnore
    private String bPhaseVoltage;//B相电压
    @ExcelProperty(value = "B相电压")
    private String bphaseVoltageOfExcel;

    @ExcelIgnore
    private String cPhaseVoltage;//C相电压
    @ExcelProperty(value = "C相电压")
    private String cphaseVoltageOfExcel;

    @ExcelIgnore
    private String aPhaseCurrent;//A相电流
    @ExcelProperty(value = "A相电流")
    private String aphaseCurrentOfExcel;

    @ExcelIgnore
    private String bPhaseCurrent;//B相电流
    @ExcelProperty(value = "B相电流")
    private String bphaseCurrentOfExcel;

    @ExcelIgnore
    private String cPhaseCurrent;//C相电流
    @ExcelProperty(value = "C相电流")
    private String cphaseCurrentOfExcel;

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
    @ExcelProperty(value = "频率")
    private String frequency;
    @ExcelProperty(value = "AB线电压")
    private String abLineVoltage;//AB线电压
    @ExcelProperty(value = "BC线电压")
    private String bcLineVoltage;//BC线电压
    @ExcelProperty(value = "CA线电压")
    private String caLineVoltage;//CA线电压
    @ExcelProperty(value = "失压数据HB")
    private Short lossVoltData;//失压数据HB

    @ExcelIgnore
    private String aPhaseActivePower;//A相有功功率
    @ExcelProperty(value = "A相有功功率")
    private String aphaseActivePowerOfExcel;

    @ExcelIgnore
    private String bPhaseActivePower;//B相有功功率
    @ExcelProperty(value = "B相有功功率")
    private String bphaseActivePowerOfExcel;

    @ExcelIgnore
    private String cPhaseActivePower;//C相有功功率
    @ExcelProperty(value = "C相有功功率")
    private String cphaseActivePowerOfExcel;

    @ExcelProperty(value = "总有功功率")
    private String totalActivePower;//总有功功率

   @ExcelIgnore
    private String aPhaseReactivePower;//A相无功功率
    @ExcelProperty(value = "A相无功功率")
    private String aphaseReactivePowerOfExcel;

    @ExcelIgnore
    private String bPhaseReactivePower;//B相无功功率
    @ExcelProperty(value = "B相无功功率")
    private String bphaseReactivePowerOfExcel;

    @ExcelIgnore
    private String cPhaseReactivePower;//C相无功功率
    @ExcelProperty(value = "C相无功功率")
    private String cphaseReactivePowerOfExcel;

    @ExcelProperty(value = "总无功功率")
    private String totalReactivePower;//总无功功率

    @ExcelIgnore
    private String aPhaseDependPower;//A相视在功率
    @ExcelProperty(value = "A相视在功率")
    private String aphaseDependPowerOfExcel;

    @ExcelIgnore
    private String bPhaseDependPower;//B相视在功率
    @ExcelProperty(value = "B相视在功率")
    private String bphaseDependPowerOfExcel;

    @ExcelIgnore
    private String cPhaseDependPower;//C相视在功率
    @ExcelProperty(value = "C相视在功率")
    private String cphaseDependPowerOfExcel;

    @ExcelProperty(value = "总视在功率")
    private String totalDependPower;//总视在功率

    @ExcelIgnore
    private String aPhasePowerFactor;
    @ExcelProperty(value = "A相功率因数")
    private String aphasePowerFactorOfExcel;

    @ExcelIgnore
    private String bPhasePowerFactor;
    @ExcelProperty(value = "B相功率因数")
    private String bphasePowerFactorOfExcel;

    @ExcelIgnore
    private String cPhasePowerFactor;
    @ExcelProperty(value = "C相功率因数")
    private String cphasePowerFactorOfExcel;

    @ExcelProperty(value = "总功率因数")
    private String totalPowerFactor;//总功率因数

    //"-1 代表 无 0 代表 公网   1 代表 储能箱放电
    @ExcelProperty(value = "负荷电表用电来源")
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
    public String getAphaseVoltageOfExcel() {
        return aPhaseVoltage;
    }

    public String getBphaseVoltageOfExcel() {
        return bPhaseVoltage;
    }

    public String getCphaseVoltageOfExcel() {
        return cPhaseVoltage;
    }

    public String getAphaseCurrentOfExcel() {
        return aPhaseCurrent;
    }

    public String getBphaseCurrentOfExcel() {
        return bPhaseCurrent;
    }

    public String getCphaseCurrentOfExcel() {
        return cPhaseCurrent;
    }

    public String getAphaseActivePowerOfExcel() {
        return aPhaseActivePower;
    }

    public String getBphaseActivePowerOfExcel() {
        return bPhaseActivePower;
    }

    public String getCphaseActivePowerOfExcel() {
        return cPhaseActivePower;
    }

    public String getAphaseReactivePowerOfExcel() {
        return aPhaseReactivePower;
    }

    public String getBphaseReactivePowerOfExcel() {
        return bPhaseReactivePower;
    }

    public String getCphaseReactivePowerOfExcel() {
        return cPhaseReactivePower;
    }

    public String getAphaseDependPowerOfExcel() {
        return aPhaseDependPower;
    }

    public String getBphaseDependPowerOfExcel() {
        return bPhaseDependPower;
    }

    public String getCphaseDependPowerOfExcel() {
        return cPhaseDependPower;
    }

    public String getAphasePowerFactorOfExcel() {
        return aPhasePowerFactor;
    }

    public String getBphasePowerFactorOfExcel() {
        return bPhasePowerFactor;
    }

    public String getCphasePowerFactorOfExcel() {
        return cPhasePowerFactor;
    }
}
