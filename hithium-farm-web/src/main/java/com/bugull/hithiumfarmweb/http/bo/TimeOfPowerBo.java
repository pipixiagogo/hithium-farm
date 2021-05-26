package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("时间段功率实体类")
public class TimeOfPowerBo {
    @ApiModelProperty(value = "功率值")
    private String power;
    @ApiModelProperty(value = "时间段")
    private String time;

    @ApiModelProperty(value = "充放电设置 充电0  放电1")
    private Integer type;

    @ApiModelProperty(value = "设置设备运行状态模式 0:峰谷 1:备电 2:指令 3:计划")
    private Integer runMode;
}
