package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PowerInstructions  {

    private String id;
    private String orderId;

    private List<Integer> powerStrategy;

    private String deviceName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date strategyTime;

    private String accessToken;

    private Date orderTime = new Date(System.currentTimeMillis());

    private Boolean settingResult = false;
    //0:默认功率 1:设定功率
    private Integer type;
}
