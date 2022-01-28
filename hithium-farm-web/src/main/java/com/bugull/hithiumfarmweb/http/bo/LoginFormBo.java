package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;


@Data
public class LoginFormBo {
    private String userName;
    private String password;
    private String captcha;
    private String uuid;
}
