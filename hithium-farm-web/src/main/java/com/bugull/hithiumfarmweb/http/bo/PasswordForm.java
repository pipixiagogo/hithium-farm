/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 密码表单
 */
@Data
public class PasswordForm {
    /**
     * 原密码
     */
    @NotBlank(message = "旧密码为空",groups = UpdateGroup.class)
    private String password;
    /**
     * 新密码
     */
    @NotBlank(message = "新密码为空",groups = UpdateGroup.class)
    private String newPassword;

}
