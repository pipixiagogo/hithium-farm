package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceVo {

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "数量")
    private Integer num;

//    @ApiModelProperty(value = "城市信息")
    private List<CityVo> cityVoList;

}
