package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeCapacityStationVo {

    private String chargeCapacity;

    private String disChargeCapacity;

    private String stationName;

    private String totalStationIncome = new BigDecimal(0).toString();

    private String dayStationIncome = new BigDecimal(0).toString();


}
