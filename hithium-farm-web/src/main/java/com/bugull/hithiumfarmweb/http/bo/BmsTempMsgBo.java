package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BmsTempMsgBo {

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "温度")
    private String temperature;

    @ApiModelProperty(value = "湿度")
    private String humidity;

    @ApiModelProperty(value = "发生时间")
    private Date generationDataTime;
    @ApiModelProperty(value = "设备编号")
    private Integer equipmentId;

}
