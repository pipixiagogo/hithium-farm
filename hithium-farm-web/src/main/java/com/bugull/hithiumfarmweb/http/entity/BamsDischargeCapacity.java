package com.bugull.hithium.core.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class BamsDischargeCapacity extends SimpleEntity {
    private String deviceName;

    private Date generationDataTime;

    private BigDecimal dischargeCapacitySum;

    private BigDecimal chargeCapacitySum;

    private Integer equipmentId;

    private BigDecimal dischargeCapacitySubtract;

    private BigDecimal chargeCapacitySubtract;



 }
