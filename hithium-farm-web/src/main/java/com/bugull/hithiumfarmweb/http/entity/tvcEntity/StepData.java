package com.bugull.hithiumfarmweb.http.entity.tvcEntity;

import lombok.Data;

import java.util.Date;

@Data
public class StepData  {
    private String codeNumber;

    private String stepNumber;

    private String stepModel;
    private Date stepTime;
    private String capacity;
    private String energy;
    private String medianVoltage;
    private String startVoltage;
    private String endVoltage;

    private Date testTime;
    private String deviceNo;

}
