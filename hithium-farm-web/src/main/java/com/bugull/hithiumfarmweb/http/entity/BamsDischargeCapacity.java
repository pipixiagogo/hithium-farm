package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BamsDischargeCapacity{
    private String deviceName;

    private Date generationDataTime;

    private BigDecimal dischargeCapacitySum;

    private BigDecimal chargeCapacitySum;

    private Integer equipmentId;

    private BigDecimal dischargeCapacitySubtract;

    private BigDecimal chargeCapacitySubtract;



 }
