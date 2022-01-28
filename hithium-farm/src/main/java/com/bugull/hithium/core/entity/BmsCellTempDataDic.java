package com.bugull.hithium.core.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 单体温度
 */
@Data
public class BmsCellTempDataDic {
//    Time	DateTime
//    EquipmentId	int
//    EquipChannelStatus	int
    private String deviceName;
    //设备名称
    private String name;
    private String time;
    private Date generationDataTime;
    //设备名称
    private Integer equipmentId;
    //前置机连接的设备的通道状态	"0-正常，1-异常
    //默认值异常
    //"
    private Integer equipChannelStatus;

    //温度列表
    private Map<String,Integer> tempMap;

}
