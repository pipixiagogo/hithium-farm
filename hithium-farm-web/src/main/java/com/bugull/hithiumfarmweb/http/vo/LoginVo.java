package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
public class LoginVo {
    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "token过期时间")
    private Date tokenExpireTime;

    @ApiModelProperty(value = "refreshToken")
    private String refreshToken;

    @ApiModelProperty(value = "refreshToken过期时间")
    private Date refreshTokenExpireTime;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "用户类型  内部 0 外部 1 测试 2")
    private Integer userType;
}
