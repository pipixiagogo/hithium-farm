package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class UserVo {

    private String userName;
    private String token;
    private Date createTime;

    private Date tokenExpireTime;

    private String mobile;
    private String email;
}
