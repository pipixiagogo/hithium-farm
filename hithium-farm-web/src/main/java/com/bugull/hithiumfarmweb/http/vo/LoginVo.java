package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.ApiModelProperty;
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
}
