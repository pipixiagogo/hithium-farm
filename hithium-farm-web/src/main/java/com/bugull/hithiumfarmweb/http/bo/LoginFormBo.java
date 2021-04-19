package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginFormBo {
    @NotNull(message = "用户名称为空", groups = {UpdateGroup.class})
    private String userName;
    @NotNull(message = "密码为空", groups = {UpdateGroup.class})
    private String password;
    @NotNull(message = "验证码为空", groups = {UpdateGroup.class})
    private String captcha;
    @NotNull(message = "UUID为空", groups = {UpdateGroup.class})
    private String uuid;
}
