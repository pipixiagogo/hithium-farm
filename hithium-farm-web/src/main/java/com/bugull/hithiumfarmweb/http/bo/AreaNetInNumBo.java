package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AreaNetInNumBo {
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "类型 月份 MONTH  季度:QUARTER")
    private String type;
    @ApiModelProperty(value = "国家")
    private String country="china";

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "市区")
    private String city;

}
