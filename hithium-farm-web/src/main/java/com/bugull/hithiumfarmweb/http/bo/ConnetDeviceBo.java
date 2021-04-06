package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data

public class ConnetDeviceBo {
    @ApiModelProperty(name = "connetName", required = true, example = " ", value = "连接白名单设备名称")
    @NotBlank(message = "连接白名单设备名称", groups = {AddGroup.class})
    private String connetName;
    @ApiModelProperty(name = "projectType", required = true, example = " ", value = "项目类型不能为空")
    @NotBlank(message = "项目类型不能为空", groups = {AddGroup.class})
    private String projectType;

    private String topic;
}
