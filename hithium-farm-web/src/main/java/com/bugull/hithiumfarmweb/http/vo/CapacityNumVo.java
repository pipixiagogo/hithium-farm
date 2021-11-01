package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

@Data
public class CapacityNumVo {

    /**
     * 充电电量累加值
     */
    private String chargeCapacitySum = "0";

    /**
     * 放电电量累加值
     */
    private String dischargeCapacitySum = "0";
    private String totalChargeSum = "0";

    private String windEnergyChargeSum = "0";

    private String photovoltaicChargeSum = "0";

}
