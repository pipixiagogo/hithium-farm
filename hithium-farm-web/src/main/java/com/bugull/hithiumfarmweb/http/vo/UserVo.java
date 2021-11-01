package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVo {
    private String userName;
    private String token;
    private Date createTime;

    private Date tokenExpireTime;
//
//    private String mobile;
//    private String email;
}
