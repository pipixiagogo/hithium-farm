package com.bugull.hithium.core.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 单体电压
 */
@Data
public class BmsCellVoltDataDic {
    /**
     * Time	DateTime	时间	默认值DateTime.Now;
     * EquipmentId	int	设备名称
     * EquipChannelStatus	int	前置机连接的设备的通道状态	"0-正常，1-异常
     * 默认值异常“
     */
    private String time;
    private Date generationDataTime;

    private Integer equipmentId;

    private Integer equipChannelStatus;

    private String name;
    private String deviceName;

    //单体电压
    private Map<String,Integer> volMap;

}
