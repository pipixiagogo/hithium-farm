package com.bugull.hithiumfarmweb.http.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BreakDownLogVo {
    @ApiModelProperty("储能站设备名称")
    @ExcelProperty(value = "储能站名称")
    private String deviceOfName;
    @ApiModelProperty("设备描述")
    @ExcelProperty(value = "设备描述")
    private String description;
    @ExcelIgnore
    @ApiModelProperty(value = "设备码 唯一")
    private String deviceName;
    @ExcelProperty(value = "设备名称")
    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "告警类型")
    @ExcelProperty(value = "告警类型")
    private String alarmTypeMsg;

    @ExcelProperty(value = "时间")
    @ApiModelProperty("告警日志产生时间")
    private Date generationDataTime;

    //告警消息
    @ExcelProperty(value = "告警信息")
    @ApiModelProperty("告警信息")
    private String message;

    //详细信息
    @ExcelProperty(value = "详细信息")
    @ApiModelProperty("详细信息")
    private String detailInfomation;
    //状态
    @ExcelProperty(value = "状态")
    @ApiModelProperty("状态")
    private String statusMsg;

    //恢复原因
    @ExcelProperty(value = "恢复原因")
    @ApiModelProperty("恢复原因")
    private String removeReason;
    @ExcelProperty(value = "恢复时间")
    @ApiModelProperty("恢复时间")
    private Date removeData;

    //备注
    @ExcelProperty(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 所属地区
     */
    @ApiModelProperty("所属地区")
    @ExcelProperty(value = "所属地区")
    private String areaMsg;




}
