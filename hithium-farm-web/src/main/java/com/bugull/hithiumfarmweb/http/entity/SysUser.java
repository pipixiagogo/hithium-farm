package com.bugull.hithiumfarmweb.http.entity;


import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.*;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Entity
@EnsureIndex("{userName:1}")
public class SysUser implements BuguEntity {

    @Id(type = IdType.AUTO_INCREASE, start = 1L)
    private String id;

    @NotBlank(message = "用户名称不能为空", groups = {UpdateGroup.class, AddGroup.class})
    private String userName;

    @NotBlank(message = "用户密码不能为空", groups = {UpdateGroup.class, AddGroup.class})
    private String password;
    private String salt;

    private String mobile;
    @NotBlank(message="邮箱不能为空", groups = { UpdateGroup.class, AddGroup.class})
    @Email(message="邮箱格式不正确", groups = { UpdateGroup.class,AddGroup.class})
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
    @Ignore
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
