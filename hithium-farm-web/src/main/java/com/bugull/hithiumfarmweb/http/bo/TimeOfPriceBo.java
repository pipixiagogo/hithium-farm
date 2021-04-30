package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.mongo.annotations.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("时间段价格实体类")
public class TimeOfPriceBo {
    @ApiModelProperty(value = "电价价格")
    private String price;
    @ApiModelProperty(value = "时间段")
    private String time;
    @ApiModelProperty(name = "type",value = "电价类型 0:尖时 1:峰时 2:平时 3:谷时")
    private Integer type;
}
