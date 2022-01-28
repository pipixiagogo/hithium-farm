package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperationLogEntity {
    //用户名
    private String username;
    //用户操作
    private String operation;
    //执行时长(毫秒)
    private Long time;
    //创建时间
    private Date createDate;
}
