package com.bugull.hithiumfarmweb.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PowerInstructions {

    private String orderId;

    private List<Integer> powerStrategy;

    private String deviceName;

    private Date strategyTime;

    private String accessToken;

    private Date orderTime = new Date(System.currentTimeMillis());

    private Boolean settingResult = false;
}
