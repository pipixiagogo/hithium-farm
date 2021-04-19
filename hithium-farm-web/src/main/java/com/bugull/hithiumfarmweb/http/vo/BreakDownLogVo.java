package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BreakDownLogVo {

    @ApiModelProperty(value = "设备码 唯一")
    private String deviceName;

    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "告警类型")
    private String alarmTypeMsg;

    @ApiModelProperty("告警日志产生时间")
    private Date generationDataTime;

    //告警消息
    @ApiModelProperty("告警信息")
    private String message;

    //详细信息
    @ApiModelProperty("详细信息")
    private String detailInfomation;
    //状态
    @ApiModelProperty("状态")
    private String statusMsg;

    //恢复原因
    @ApiModelProperty("恢复原因")
    private String removeReason;
    @ApiModelProperty("恢复时间")
    private Date removeData;

    //备注
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 所属地区
     */
    @ApiModelProperty("所属地区")
    private String areaMsg;

    @ApiModelProperty("储能站设备名称")
    private String deviceOfName;

    @ApiModelProperty("设备描述")
    private String description;
}
