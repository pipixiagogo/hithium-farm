package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

@Data
public class RoleEntityOfUserBo {

    private String id;
    private String roleName;

    private String remark;

    private String createTime;
}
