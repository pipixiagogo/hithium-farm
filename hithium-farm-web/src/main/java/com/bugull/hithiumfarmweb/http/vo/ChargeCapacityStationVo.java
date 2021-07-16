package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChargeCapacityStationVo {

    @ApiModelProperty(value = "电池堆充电量")
    private String chargeCapacity;

    @ApiModelProperty(value = "电池堆放电量")
    private String disChargeCapacity;

    @ApiModelProperty(value = "电站名称")
    private String stationName;


}
