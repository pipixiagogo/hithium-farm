package com.bugull.hithiumfarmweb.http.controller.pub.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import lombok.Data;

import java.util.Date;

@Entity(name = "bcudatadicbcu")
@Data
public class BcuEntity implements BuguEntity {
    @Id
    private String id;
    private String name;
    private String deviceName;
    private String groupCurrent;//簇电流
    private String avgSingleTemperature;//单体温度平均值
    private String maxSingleTemperature;//单体温度最大值
    private String cellTemperatureDiff;//温差
    private Date generationDataTime;
}
