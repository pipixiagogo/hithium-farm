package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AlarmTime {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String accessToken;
}
