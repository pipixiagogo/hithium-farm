package com.bugull.hithiumfarmweb.http.bo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(description = "设备功率时间段实体类")
public class ModifyDevicePowerBo {

    @ApiModelProperty(name = "deviceNames",value = "设备码 设备唯一标识 多台设备使用,分隔")
    private String deviceNames;

    @ApiModelProperty(name ="powerOfTime",value = " key:运行模式 0:峰谷 1:备电 2:指令 3:计划 value:时间段功率实体类集合")
    private Map<Integer,List<TimeOfPowerBo>> powerOfTime;
}
