package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InfoUserVo {

    private String id;
    private String userName;
    private Date createTime;

    private String mobile;
    private String email;

    private List<RoleEntityOfUserBo> roleEntityLsit;
    //    账号状态  0：禁用   1：正常
    private Integer status;
    private String remarks;
    private Date userExpireTime;

    private List<EssStation> essStationList;

    private Integer userType;


}
