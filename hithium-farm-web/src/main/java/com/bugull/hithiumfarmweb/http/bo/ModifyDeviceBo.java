package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "设备价格时间段实体类")
public class ModifyDeviceBo {

    @ApiModelProperty(name = "deviceName",value = "设备码 设备唯一标识")
    private String deviceName;

    @ApiModelProperty(name ="priceOfTime",value = "时间段字符 08:00-12:00,12:00-13:00 可用,分割  价格")
    private List<TimeOfPriceBo>priceOfTime;
}
