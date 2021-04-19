package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticBo {

    @ApiModelProperty(value = "时间")
    private String date;
    @ApiModelProperty(value = "数量")
    private int count = 0;
}
