package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class IncomeEntity extends SimpleEntity {


    private String deviceName;

    private BigDecimal income;

    private String incomeOfDay;

    private String province;

    private String city;
}
