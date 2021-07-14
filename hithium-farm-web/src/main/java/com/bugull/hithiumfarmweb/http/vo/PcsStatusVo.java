package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PcsStatusVo {

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备ID")
    private Integer equipmentId;

    @ApiModelProperty(value = "有功功率")
    private Integer activePower;

    @ApiModelProperty(value = "充放电状态")
    private String chargeDischargeStatusMsg;

    @ApiModelProperty(value = "无功功率")
    private Integer reactivePower;

    @ApiModelProperty(value = "交流电压")
    private String exchangeVol;

    @ApiModelProperty(value = "交流电流")
    private String exchangeCurrent;

    @ApiModelProperty(value = "交流频率")
    private String exchangeRate;

    @ApiModelProperty(value = "直流电压")
    private String voltage;

    @ApiModelProperty(value = "直流电流")
    private String current;
    /**
     * 直流功率=直流电压*直流电流
     */
    @ApiModelProperty(value = "直流功率")
    private String power;

    @ApiModelProperty(value = "运行状态")
    private String runningStatusMsg;

    @ApiModelProperty(value = "SOC")
    private String soc;


}
