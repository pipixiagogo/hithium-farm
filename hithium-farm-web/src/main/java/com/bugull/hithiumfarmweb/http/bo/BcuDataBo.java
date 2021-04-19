package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
public class BcuDataBo {

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "数据产生时间")
    private Date generationDataTime;

    /**
     * 电池簇充放电状态
     */
    @ApiModelProperty(value = "电池簇充放电状态")
    private String groupChargeDischargeStatusMsg;
    /**
     * 电池簇运行状态
     */
    @ApiModelProperty(value = "电池簇运行状态")
    private String groupRunStatusMsg;
    /**
     * 簇端电压
     */
    @ApiModelProperty(value = "簇端电压")
    private String groupVoltage;
    /**
     * 簇端电流
     */
    @ApiModelProperty(value = "簇端电流")
    private String groupCurrent;

    /**
     * 电池簇管辖 温度节数
     */
    @ApiModelProperty(value = "电池簇管辖 温度节数")
    private Short groupTemperatureCount;
    /**
     * 电池簇管辖 电池节数
     */
    @ApiModelProperty(value = "电池簇管辖 电池节数")
    private Short groupBatteryCount;
    /**
     * 簇SOC
     */
    @ApiModelProperty(value = "簇SOC")
    private String groupSoc;

    /**
     * 簇可充电量
     */
    @ApiModelProperty(value = "簇可充电量")
    private String groupRechargeableQuantity;
    /**
     * 簇可放电量
     */
    @ApiModelProperty(value = "簇可放电量")
    private String groupRedischargeableQuantity;

    /**
     * 簇充电电量累加值
     */
    @ApiModelProperty(value = "簇充电电量累加值")
    private String groupAccuChargeQuantity;
    /**
     * 簇放电电量累加值
     */
    @ApiModelProperty(value = "簇放电电量累加值")
    private String groupAccuDischargeQuantity;

    /**
     * 单体电压平均值
     */
    @ApiModelProperty(value = "单体电压平均值")
    private String avgSingleVoltage;
    /**
     * 最高电压
     */
    @ApiModelProperty(value = "最高电压")
    private String maxSingleVoltage;
    /**
     * 电压最大节号
     */
    @ApiModelProperty(value = "电压最大节号")
    private Short maxIndexVoltage;
    /**
     * 电压最低节号
     */
    @ApiModelProperty(value = "电压最低节号")
    private Short minIndexVoltage;
    /**
     * 最低电压
     */
    @ApiModelProperty(value = "最低电压")
    private String minSingleVoltage;

    /**
     * 平均温度
     */
    @ApiModelProperty(value = "平均温度")
    private String avgSingleTemperature;
    /**
     * 单体温度最大值
     */
    @ApiModelProperty(value = "单体温度最大值")
    private String maxSingleTemperature;

    /**
     * 最大温度节号
     */
    @ApiModelProperty(value = "最大温度节号")
    private Short maxIndexTemperature;
    /**
     * 最小温度节号
     */
    @ApiModelProperty(value = "最小温度节号")
    private Short minIndexTemperature;
    /**
     * 单体温度最小值
     */
    @ApiModelProperty(value = "单体温度最小值")
    private String minSingleTemperature;

}
