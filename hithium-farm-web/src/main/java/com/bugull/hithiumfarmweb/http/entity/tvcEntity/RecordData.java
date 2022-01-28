package com.bugull.hithiumfarmweb.http.entity.tvcEntity;

import lombok.Data;

import java.util.Date;

@Data
public class RecordData {
    private String codeNumber;
    private String recordNumber;

    private Date testTime;
    private Date stepTime;

    private String electricCurrent;

    private String capacity;

    private String soc;
    private String voltage;

    private String energy;
    private String auxTemperature;

    private Date systemTime;
    private Integer circleNumber;

    private String stepNumber;
    private String stepStatus;


    private String deviceNo;
}
