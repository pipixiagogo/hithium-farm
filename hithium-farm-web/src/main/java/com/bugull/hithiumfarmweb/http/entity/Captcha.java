package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Date;

/**
 * 验证码实体类
 */
@Data
public class Captcha {

    private String uuid;
    /**
     * 验证码
     */
    private String code;
    /**
     * 过期时间
     */
    private Date expireTime;
}
