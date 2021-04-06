package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;

@Data
public class InfoUserVo {
    private String userName;
    private Date createTime;

    private String mobile;
    private String email;
}
