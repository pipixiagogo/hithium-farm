package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityNumBo {

    /**
     * 充电电量累加值
     */
    @ApiModelProperty(value = "充电电量累加值")
    private String chargeCapacitySum;

    /**
     * 放电电量累加值
     */
    @ApiModelProperty(value = "dischargeCapacitySum")
    private String dischargeCapacitySum;
}
