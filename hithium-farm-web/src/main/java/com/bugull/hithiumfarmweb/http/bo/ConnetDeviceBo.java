package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data

public class ConnetDeviceBo {
    @NotBlank(message = "连接白名单设备名称", groups = {AddGroup.class})
    private String connetName;
    @NotBlank(message = "项目类型不能为空", groups = {AddGroup.class})
    private String projectType;

    private String topic;
}
