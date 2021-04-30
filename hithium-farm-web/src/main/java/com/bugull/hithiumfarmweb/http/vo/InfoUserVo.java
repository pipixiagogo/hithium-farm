package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InfoUserVo {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

//    @ApiModelProperty(value = "手机号")
//    private String mobile;
//    @ApiModelProperty(value = "邮箱")
//    private String email;

    @ApiModelProperty(value = "角色实体类")
    private List<RoleEntityOfUserBo> roleEntityLsit;
}
