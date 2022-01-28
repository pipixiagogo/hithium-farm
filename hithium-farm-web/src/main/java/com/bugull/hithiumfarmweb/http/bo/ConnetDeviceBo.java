package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import lombok.Data;


@Data

public class ConnetDeviceBo {
    private String connetName;
    private String projectType;

    private String topic;
}
