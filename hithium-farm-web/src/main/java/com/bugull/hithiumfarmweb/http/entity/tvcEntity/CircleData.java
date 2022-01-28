package com.bugull.hithiumfarmweb.http.entity.tvcEntity;

import lombok.Data;

import java.util.Date;

@Data
public class CircleData  {
    private Integer circleNumber;
    private String codeNumber;

    private String chargingCapacity;

    private String disChargingCapacity;

    private String efficiency;

    private String chargingEnergy;

    private String disChargingEnergy;

    private String chargingMediumVoltage;

    private String disChargingMediumVoltage;

    private String constantCurrentInflow;

    private String constantCurrentImpulseRatio;

    private String rd;

    private String rd2;

    private String finalDischargeVoltage;

    private String capacityRetention;

    private String chargingDCIR;
    private String disChargingDCIR;

    private Date  testTime;
    private String deviceNo;

}
