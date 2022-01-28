package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import lombok.Data;


@Data
public class AlarmThirdPartyVo {

    private String deviceName;
    private Integer equipmentId;
    private Long generationDataTime;
    private Integer alarmType;
    private String message;

    //详细信息
    private String detailInformation;

    private String removeReason;

    private Long removeDate;
    //备注
    private String remark;
    private Boolean status;

    private String name;
}
