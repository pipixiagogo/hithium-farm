package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 单体温度
 */
@Data
@Entity
@EnsureIndex("{deviceName:1,equipmentId:1,generationDataTime:-1}")
public class BmsCellTempDataDic extends SimpleEntity {
//    Time	DateTime
//    EquipmentId	int
//    EquipChannelStatus	int
    private String deviceName;
    //设备名称
    private String name;
    @Ignore
    private String time;
    private Date generationDataTime;
    //设备名称
    private Integer equipmentId;
    //前置机连接的设备的通道状态	"0-正常，1-异常
    //默认值异常
    //"
    private Integer equipChannelStatus;

    //温度列表
    private Map<String, Integer> tempMap;

}
