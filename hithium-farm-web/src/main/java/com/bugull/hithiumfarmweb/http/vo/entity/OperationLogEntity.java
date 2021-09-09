package com.bugull.hithiumfarmweb.http.vo.entity;

import com.bugull.mongo.annotations.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "operationlog")
public class OperationLogEntity {
    //用户名
    @ApiModelProperty(value = "用户名称")
    private String username;
    @ApiModelProperty(value = "用户操作")
    //用户操作
    private String operation;
    //执行时长(毫秒)
    @ApiModelProperty(value = "执行时长")
    private Long time;
    @ApiModelProperty(value = "创建时间")
    //创建时间
    private Date createDate;
}
