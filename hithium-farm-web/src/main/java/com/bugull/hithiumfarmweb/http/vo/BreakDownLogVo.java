package com.bugull.hithiumfarmweb.http.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BreakDownLogVo {
    @ExcelProperty(value = "储能站名称")
    private String deviceOfName;
    @ExcelProperty(value = "设备描述")
    private String description;
    @ExcelIgnore
    private String deviceName;
    @ExcelProperty(value = "设备名称")
    private String name;
    @ExcelProperty(value = "告警类型")
    private String alarmTypeMsg;

    @ExcelProperty(value = "时间")
    private Date generationDataTime;

    //告警消息
    @ExcelProperty(value = "告警信息")
    private String message;

    //详细信息
    @ExcelProperty(value = "详细信息")
    private String detailInfomation;
    //状态
    @ExcelProperty(value = "状态")
    private String statusMsg;

    //恢复原因
    @ExcelProperty(value = "恢复原因")
    private String removeReason;
    @ExcelProperty(value = "恢复时间")
    private Date removeData;

    //备注
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 所属地区
     */
    @ExcelProperty(value = "所属地区")
    private String areaMsg;




}
