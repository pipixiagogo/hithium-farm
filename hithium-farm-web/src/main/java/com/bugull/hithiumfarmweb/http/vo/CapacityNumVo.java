package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityNumVo {

    /**
     * 充电电量累加值
     */
    @ApiModelProperty(value = "充电电量累加值")
    private String chargeCapacitySum;

    /**
     * 放电电量累加值
     */
    @ApiModelProperty(value = "放电电量累加值")
    private String dischargeCapacitySum;
}
