package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

@Data
public class TimeOfPowerBo {
    private String power;
    private String time;

    private Integer type;

    private Integer runMode;
}
