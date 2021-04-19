package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BamsLatestBo {


    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "数据产生时间")
    private Date generationDataTime;
    @ApiModelProperty(value = "soc")
    private String soc;
    /**
     * 总电压除以电芯个数
     */
    @ApiModelProperty(value = "平均电压")
    private String voltage;
    /**
     * 可充电量
     */
    @ApiModelProperty(value = "可充电量")
    private String chargeCapacity;

    /**
     * 可放电量
     */
    @ApiModelProperty(value = "可放电量")
    private String dischargeCapacity;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private String chargeCapacitySum;

    /**
     * 累计放电量
     */
    @ApiModelProperty(value = "累计放电量")
    private String dischargeCapacitySum;
    /**
     * 最高单体电压
     */
    @ApiModelProperty(value = "最高单体电压")
    private String maxCellVol;
    @ApiModelProperty(value = "最高单体电压电池簇号")
    private Short maxCellVolBcuIdx;
    @ApiModelProperty(value = "最高单体电压所在簇的簇内编号")
    private Short maxCellVolCellIdx;
    /**
     * 最低单体电压
     */
    @ApiModelProperty(value = "最低单体电压")
    private String minCellVol;
    @ApiModelProperty(value = "最低单体电压电池簇号")
    private String minCellVolBcuIdx;
    @ApiModelProperty(value = "最低单体电压所在簇的簇内编号")
    private String minCellVolCellIdx;
    /**
     * 最高电池温度
     */
    @ApiModelProperty(value = "最高电池温度")
    private String maxCellTemp;
    @ApiModelProperty(value = "最高单体温度电池簇号")
    private String maxCellTempBcuIdx;
    @ApiModelProperty(value = " 最高单体温度所在簇的簇内编号")
    private String maxCellTempCellIdx;

    /**
     * 最低电池温度
     */
    @ApiModelProperty(value = "最低电池温度")
    private String minCellTemp;
    @ApiModelProperty(value = " 最低单体温度电池簇号")
    private String minCellTempBcuIdx;
    @ApiModelProperty(value = " 最低单体温度所在簇的簇内编号")
    private String minCellTempCellIdx;

    /**
     * 电池堆运行状态
     */
    @ApiModelProperty(value = "电池堆运行状态")
    private String runningStateMsg;


}
