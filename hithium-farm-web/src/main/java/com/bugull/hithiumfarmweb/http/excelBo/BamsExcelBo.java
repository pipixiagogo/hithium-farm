package com.bugull.hithiumfarmweb.http.excelBo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

@Data
public class BamsExcelBo {
    //设备名称
    @ExcelProperty(value = "设备名称")
    private String name;
    /**
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态
     * RunningState	int	电池堆运行状态
     */
    @ExcelProperty(value = "时间")
    private Date generationDataTime;
    @ExcelProperty(value = "运行状态")
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
}
