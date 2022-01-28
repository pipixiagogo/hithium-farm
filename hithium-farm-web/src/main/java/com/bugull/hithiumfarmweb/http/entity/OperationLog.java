package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperationLog {

    //用户名
    private String username;
    //用户操作
    private String operation;
    //请求方法
    private String method;
    //请求参数
    private String params;
    //执行时长(毫秒)
    private Long time;
    //IP地址
    private String ip;
    //创建时间
    private Date createDate;
}
