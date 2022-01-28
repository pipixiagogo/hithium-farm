package com.bugull.hithiumfarmweb.http.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.hithiumfarmweb.http.excelConverter.BamsRunningStateConverter;
import com.bugull.hithiumfarmweb.http.excelConverter.EquipStatusConverter;
import lombok.Data;

import java.util.Date;

@Data
public class BamsDataDicBA  {


    @ExcelIgnore
    private String id;
    @ExcelIgnore
    private String deviceName;
    //设备名称
    @ExcelProperty(value = "设备名称")
    private String name;
    /**
     * Time	DateTime	时间
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态
     * RunningState	int	电池堆运行状态
     */
    @ExcelIgnore
    private String time;
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @ExcelIgnore
    private Integer equipmentId;
    @ExcelProperty(value = "前置机设备通道状态", converter = EquipStatusConverter.class)
    private Integer equipChannelStatus;
    @ExcelProperty(value = "电池堆运行状态",converter = BamsRunningStateConverter.class)
    private Integer runningState;

    /**
     * MaxSocBcuIdx	ushort	簇SOC最大簇号
     * MinSocBcuIdx	ushort	簇SOC最小簇号
     * MaxVolBcuIdx	ushort	簇电压最大簇号
     * MinVolBcuIdx	ushort	簇电压最小簇号
     * MaxCellVolBcuIdx	ushort	最高单体电压电池簇号
     * MaxCellVolCellIdx	ushort	最高单体电压所在簇的簇内编号
     * <p>
     * MinCellVolBcuIdx	ushort	最低单体电压电池簇号
     * MinCellVolCellIdx	ushort	最低单体电压所在簇的簇内编号
     * <p>
     * MaxCellTempBcuIdx	ushort	最高单体温度电池簇号
     * MaxCellTempCellIdx	ushort	 最高单体温度所在簇的簇内编号
     */
    //簇SOC最大簇号
    @ExcelProperty(value = "簇SOC最大簇号")
    private Short maxSocBcuIdx;
    @ExcelProperty(value = "簇SOC最小簇号")
    private Short minSocBcuIdx;//簇SOC最小簇号
    @ExcelProperty(value = "簇电压最大簇号")
    private Short maxVolBcuIdx;//簇电压最大簇号
    @ExcelProperty(value = "簇电压最小簇号")
    private Short minVolBcuIdx;//簇电压最小簇号
    @ExcelProperty(value = "最高单体电压电池簇号")
    private Short maxCellVolBcuIdx;//最高单体电压电池簇号
    @ExcelProperty(value = "最高单体电压所在簇的簇内编号")
    private Short maxCellVolCellIdx;//最高单体电压所在簇的簇内编号
    @ExcelProperty(value = "最低单体电压电池簇号")
    private Short minCellVolBcuIdx;//最低单体电压电池簇号
    @ExcelProperty(value = "最低单体电压所在簇的簇内编号")
    private Short minCellVolCellIdx;//最低单体电压所在簇的簇内编号
    @ExcelProperty(value = "最高单体温度电池簇号")
    private Short maxCellTempBcuIdx;//最高单体温度电池簇号
    @ExcelProperty(value = "最高单体温度所在簇的簇内编号")
    private Short maxCellTempCellIdx;//最高单体温度所在簇的簇内编号
    /**
     MinCellTempBcuIdx	ushort	 最低单体温度电池簇号
     MinCellTempCellIdx	ushort	 最低单体温度所在簇的簇内编号
     Voltage	String	 电池堆电压
     Current	String	 电池堆电流
     Soc	String	 电池堆SOC
     Soh	String	 电池堆SOH
     ChargeCapacity	String	 电池堆可充电量
     DischargeCapacity	String	 电池堆可放电量
     CurChargeCapacity	String	 电池堆单次充电电量
     CurDischargeCapacity	String	 电池堆单次放电电量
     ChargeCapacitySum	String	 电池堆充电量累加值
     DischargeCapacitySum	String	 电池堆放电量累加值
     CellVolDiff	String	 单体电压压差极差值
     MaxCellVol	String	 最高单体电压
     MinCellVol	String	 最低单体电压
     */
    @ExcelProperty(value = "最低单体温度电池簇号")
    private Short minCellTempBcuIdx;//最低单体温度电池簇号
    @ExcelProperty(value = "最低单体温度所在簇的簇内编号")
    private Short minCellTempCellIdx;//最低单体温度所在簇的簇内编号
    @ExcelProperty(value = "电池堆电压")
    private String voltage;//电池堆电压
    @ExcelProperty(value = "电池堆电流")
    private String current;//电池堆电流
    @ExcelProperty(value = "电池堆SOC")
    private String soc;//电池堆SOC
    @ExcelProperty(value = "电池堆SOH")
    private String soh;//电池堆SOH
    @ExcelProperty(value = "电池堆可充电量")
    private String chargeCapacity;//电池堆可充电量
    @ExcelProperty(value = "电池堆可放电量")
    private String dischargeCapacity;//电池堆可放电量
    @ExcelProperty(value = "电池堆单次充电电量")
    private String curChargeCapacity;//电池堆单次充电电量
    @ExcelProperty(value = "电池堆单次放电电量")
    private String curDischargeCapacity;//电池堆单次放电电量
    @ExcelProperty(value = "电池堆充电量累加值")
    private String chargeCapacitySum;//电池堆充电量累加值
    @ExcelProperty(value = "电池堆放电量累加值")
    private String dischargeCapacitySum;//电池堆放电量累加值
    @ExcelProperty(value = "单体电压压差极差值")
    private String cellVolDiff;//单体电压压差极差值
    @ExcelProperty(value = "最高单体电压")
    private String maxCellVol;//最高单体电压
    @ExcelProperty(value = "最低单体电压")
    private String minCellVol;//最低单体电压
    @ExcelProperty(value = "单体温度温差极差值")
    private String cellTempDiff;//单体温度温差极差值
    @ExcelProperty(value = "最高电池温度")
    private String maxCellTemp;//最高电池温度
    @ExcelProperty(value = "最低电池温度")
    private String minCellTemp;//最低电池温度
    @ExcelProperty(value = "簇SOC差极差值")
    private String bcuSocDiff;//簇SOC差极差值
    @ExcelProperty(value = "簇SOC最大")
    private String maxBcuSoc;//簇SOC最大
    @ExcelProperty(value = "簇SOC最小")
    private String minBcuSoc;//簇SOC最小
    @ExcelProperty(value = "簇电压差极差值")
    private String bcuVolDiff;//簇电压差极差值
    @ExcelProperty(value = "簇电压最大")
    private String maxBcuVol;//簇电压最大
    @ExcelProperty(value = "簇电压最小")
    private String minBcuVol;//簇电压最小
    @ExcelProperty(value = "电池堆当前允许最大充电功率")
    private String allowedMaxChargePower;//电池堆当前允许最大充电功率
    @ExcelProperty(value = "电池堆当前允许最大放电功率")
    private String allowedMaxDischargePower;//电池堆当前允许最大放电功率
    @ExcelProperty(value = "电池堆当前允许最大充电电流")
    private String allowedMaxChargeCur;//电池堆当前允许最大充电电流
    @ExcelProperty(value = "电池堆当前允许最大放电电流")
    private String allowedMaxDischargeCur;//电池堆当前允许最大放电电流
    /**
     CellTempDiff	String	 单体温度温差极差值
     MaxCellTemp	String	 最高电池温度
     MinCellTemp	String	 最低电池温度
     BcuSocDiff	String	 簇SOC差极差值
     MaxBcuSoc	String	 簇SOC最大
     MinBcuSoc	String	 簇SOC最小
     BcuVolDiff	String	 簇电压差极差值
     MaxBcuVol	String	 簇电压最大
     MinBcuVol	String	 簇电压最小
     AllowedMaxChargePower	String	 电池堆当前允许最大充电功率
     AllowedMaxDischargePower	String	 电池堆当前允许最大放电功率
     AllowedMaxChargeCur	String	 电池堆当前允许最大充电电流
     AllowedMaxDischargeCur	String	 电池堆当前允许最大放电电流*/


}
