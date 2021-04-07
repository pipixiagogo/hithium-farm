package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

@Data
public class BcuDataBo {
    private String groupSoc;


    /**
     * 簇可充电量
     */
    private String groupRechargeableQuantity;
    /**
     * 可放电量
     */
    private String groupRedischargeableQuantity;

    /**
     * 单体电压平均值
     */
    private String avgSingleVoltage;
    /**
     * 电池簇充电电量累加值
     */
    private String groupAccuChargeQuantity;

    /**
     * 电池簇放电电量累加值
     */
    private String groupAccuDischargeQuantity;
}
