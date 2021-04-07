package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.hithiumfarmweb.annotation.SortField;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ignore;
import lombok.Data;

import java.util.Date;

/**
 * 设备告警日志
 */
@Data
@Entity
public class BreakDownLog extends SimpleEntity {
    private String deviceName;
    @SortField
    //设备ID
    private Integer equipmentId;
    //设备名称
    private String name;
    private Date generationDataTime;
    /**
     *  [Description("预警")]
     *         PreAlarm = 0,
     *         [Description("告警")]
     *         Alarm=1
     *         [Description("保护")]
     *         Protect=2
     *         [Description("故障")]
     *         Fault=3
     */
    private Integer alarmType;

    /**
     * 发生时间
     */
    @Ignore
    private String time;

    //告警消息
    private String message;

    //详细信息
    private String detailInfomation;

    //状态
    private Boolean status;

    //恢复原因
    private String removeReason;

    //恢复时间
    @Ignore
    private String removeTime;
    @SortField
    private Date removeData;
    //备注
    private String remark;
}