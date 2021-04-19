package com.bugull.hithiumfarmweb.http.vo;


import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CityVo {
    @ApiModelProperty(value = "市区")
    private String city;

    @ApiModelProperty(value = "数量")
    private Integer num;


    private List<DeviceAreaEntity> deviceAreaEntities;

}
