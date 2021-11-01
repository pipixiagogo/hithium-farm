package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LoginVo {
    private String token;

    private Date tokenExpireTime;

    private String refreshToken;

    private Date refreshTokenExpireTime;

    private String id;

    private Integer userType;
}
