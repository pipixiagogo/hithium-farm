package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChargeCapacityVo {

    @ApiModelProperty(value = "电池堆可充电量")
    private String chargeCapacity;

    @ApiModelProperty(value = "电池堆可放电量")
    private String disChargeCapacity;


    private String deviceName;


}
