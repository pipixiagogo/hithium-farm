package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityNumVo {

    /**
     * 充电电量累加值
     */
    @ApiModelProperty(value = "充电电量累加值")
    private String chargeCapacitySum = "0";

    /**
     * 放电电量累加值
     */
    @ApiModelProperty(value = "放电电量累加值")
    private String dischargeCapacitySum = "0";
    @ApiModelProperty(value = "总充电量")
    private String totalChargeSum = "0";

    @ApiModelProperty(value = "风能充电量")
    private String windEnergyChargeSum = "0";

    @ApiModelProperty(value = "光伏充电量")
    private String photovoltaicChargeSum = "0";

}
