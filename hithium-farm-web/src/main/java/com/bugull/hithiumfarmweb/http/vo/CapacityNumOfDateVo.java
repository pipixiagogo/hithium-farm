package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CapacityNumOfDateVo {
    /**
     * 放电电量根据时间进行放电增量统计
     */
    private String dischargeCapacitySum = "0";

    private String province;

    private String city;

    private String date;
}
