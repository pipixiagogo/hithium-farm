package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

@Data
public class PcsStatusVo {

    private String name;

    private Integer equipmentId;

    private Integer activePower;

    private String chargeDischargeStatusMsg;

    private Integer reactivePower;

    private String exchangeVol;

    private String exchangeCurrent;

    private String exchangeRate;

    private String voltage;

    private String current;
    /**
     * 直流功率=直流电压*直流电流
     */
    private String power;

    private String runningStatusMsg;

    private String soc;


}
