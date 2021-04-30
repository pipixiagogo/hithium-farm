package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PriceOfPercenVo {

    @ApiModelProperty(value = "时间段")
    private String time;
    @ApiModelProperty(value = "分钟")
    private String minute;
    @ApiModelProperty(name = "type",value = "电价类型 0:尖时 1:峰时 2:平时 3:谷时")
    private Integer type;
}
