package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticBo {

    @ApiModelProperty(value = "时间")
    private String date;
    @ApiModelProperty(value = "总功率")
    private Double count = 0D;


}
