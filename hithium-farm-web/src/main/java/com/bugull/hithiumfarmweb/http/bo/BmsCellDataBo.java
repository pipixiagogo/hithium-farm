package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BmsCellDataBo {

    @ApiModelProperty(value = "数据产生时间")
    private Date generationDataTime;

    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "温度列表信息")
    private Map<String,Short> tempMap;

    @ApiModelProperty(value = "电压列表信息")
    private Map<String,Short> volMap;


}
