package com.bugull.hithiumfarmweb.http.entity;


import com.bugull.hithiumfarmweb.common.IncIdEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class SysUser extends IncIdEntity<Long> {

    private String userName;

    private String password;
    private String salt;

    private String mobile;
    private String email;

    /**
     * 状态  0：禁用   1：正常
     */
    private Integer status = 1;
    private String token;
    private Date createTime;
    private Date tokenExpireTime;
    private List<String> roleIds;
    /**
     * 刷新token
     */
    private String refreshToken;
    /**
     * 刷新token过期时间
     */
    private Date refreshTokenExpireTime;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 用户账号过期时间
     */
    private String userExpireTimeStr;
    private Date userExpireTime;

    private List<String> stationList;

    /**
     * sys:user  --->代表账号管理
     * sys:device: ---> 历史数据查看与导出需要的权限
     */
    private String perms;

    private Integer userType;
}
