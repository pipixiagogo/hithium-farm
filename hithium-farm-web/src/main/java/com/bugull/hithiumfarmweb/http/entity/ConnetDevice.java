package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ConnetDevice extends SimpleEntity {

    /**
     * 连接的用户名称
     */
    private String connetName;

    /**
     * 连接设备生成时间
     */
    private Date createDate;

    /**
     * 客户端ID  保证ClinetId与ConnetName相同
     * 订阅客户端ID
     */
    private String clientId;
    /**
     * 可以订阅的topci
     */
    private String STDtopic;

    private String projectType;
    /**
     * 可以发布的主题
     */
    private String DTStopic;
}
