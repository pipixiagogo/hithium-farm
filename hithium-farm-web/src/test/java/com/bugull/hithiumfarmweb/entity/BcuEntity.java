package com.bugull.hithiumfarmweb.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BcuEntity  {
    private String id;
    private String name;
    private String deviceName;
    private String groupCurrent;//簇电流
    private String avgSingleTemperature;//单体温度平均值
    private String maxSingleTemperature;//单体温度最大值
    private Date generationDataTime;
}
