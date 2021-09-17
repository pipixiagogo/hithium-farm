package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@EnsureIndex("{deviceName:1,incomeOfDay:1}")
public class IncomeEntity extends SimpleEntity {


    private String deviceName;

    private BigDecimal income;

    private String incomeOfDay;

    private String province;

    private String city;
}
