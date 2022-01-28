package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PowerDefaultInstructions {
    private String deviceName;
    private String orderId;
    private String accessToken;
    private Date orderTime = new Date(System.currentTimeMillis());
    private Boolean settingResult = false;
    private List<Integer> powerStrategy;
}
