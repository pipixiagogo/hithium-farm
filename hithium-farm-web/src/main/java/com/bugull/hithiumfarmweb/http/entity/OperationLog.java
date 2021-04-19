package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class OperationLog extends SimpleEntity {

    //用户名
    @ApiModelProperty(value = "用户名称")
    private String username;
    @ApiModelProperty(value = "用户操作")
    //用户操作
    private String operation;
    @ApiModelProperty(value = "请求方法")
    //请求方法
    private String method;
    //请求参数
    @ApiModelProperty(value = "请求参数")
    private String params;
    //执行时长(毫秒)
    @ApiModelProperty(value = "执行时长")
    private Long time;
    //IP地址
    private String ip;
    @ApiModelProperty(value = "创建时间")
    //创建时间
    private Date createDate;
}
