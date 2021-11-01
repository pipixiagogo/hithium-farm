package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

import java.util.Date;

@Data
public class BamsLatestBo {


    private String name;
    private Date generationDataTime;
    private String soc;
    /**
     * 总电压除以电芯个数
     */
    private String voltage;
    /**
     * 可充电量
     */
    private String chargeCapacity;

    /**
     * 可放电量
     */
    private String dischargeCapacity;

    /**
     * 累计充电量
     */
    private String chargeCapacitySum;

    /**
     * 累计放电量
     */
    private String dischargeCapacitySum;
    /**
     * 最高单体电压
     */
    private String maxCellVol;
    private Short maxCellVolBcuIdx;
    private Short maxCellVolCellIdx;
    /**
     * 最低单体电压
     */
    private String minCellVol;
    private String minCellVolBcuIdx;
    private String minCellVolCellIdx;
    /**
     * 最高电池温度
     */
    private String maxCellTemp;
    private String maxCellTempBcuIdx;
    private String maxCellTempCellIdx;

    /**
     * 最低电池温度
     */
    private String minCellTemp;
    private String minCellTempBcuIdx;
    private String minCellTempCellIdx;

    /**
     * 电池堆运行状态
     */
    private String runningStateMsg;


}
