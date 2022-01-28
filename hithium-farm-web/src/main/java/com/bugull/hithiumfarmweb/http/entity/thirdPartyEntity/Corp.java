package com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Corp {

   //公司名称 深电能
    private String corpAccessName;

    private Date createTime;
    //生成16位UUID
    private String corpAccessId;
    //生成32位UUID
    private String corpAccessSecret;

    //供应商绑定的设备列表
    private List<String> deviceName;


}
