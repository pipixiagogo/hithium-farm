package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class InfoUserVo {
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    private String email;
}
