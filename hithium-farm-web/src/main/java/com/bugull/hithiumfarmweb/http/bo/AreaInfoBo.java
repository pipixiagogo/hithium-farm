package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AreaInfoBo {
    @ApiModelProperty(value = "国家")
    private String country="china";

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "市区")
    private String city;
}
