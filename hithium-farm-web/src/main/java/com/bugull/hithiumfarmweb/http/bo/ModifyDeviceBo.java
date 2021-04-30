package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.mongo.annotations.EmbedList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(description = "设备价格时间段实体类")
public class ModifyDeviceBo {

    @ApiModelProperty(name = "deviceName",value = "设备码 设备唯一标识")
    private String deviceName;

    @ApiModelProperty(name ="priceOfTime",value = "key:时间段字符 08:00-12:00,12:00-13:00 可用,分割  value:价格")
//    private Map<String,String> priceOfTime;
    private List<TimeOfPriceBo>priceOfTime;
}
