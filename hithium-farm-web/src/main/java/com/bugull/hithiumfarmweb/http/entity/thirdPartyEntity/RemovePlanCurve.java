package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RemovePlanCurve  {

    private Date removeOrderTime=new Date(System.currentTimeMillis());

    private String deviceName;

    private String orderId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date strategyTime;

    /**
     * 下发删除策略结果 结果失败继续下次校对时候下发
     */
    private Boolean removeResult = false;

    private String accessToken;

    private Integer type;

}
