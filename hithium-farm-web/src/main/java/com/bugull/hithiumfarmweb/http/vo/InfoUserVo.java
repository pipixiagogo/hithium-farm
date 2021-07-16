package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
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

    @ApiModelProperty(value = "手机号")
    private String mobile;
//    @ApiModelProperty(value = "邮箱")
//    private String email;

    @ApiModelProperty(value = "角色实体类")
    private List<RoleEntityOfUserBo> roleEntityLsit;
    //    账号状态  0：禁用   1：正常
    @ApiModelProperty(value = "账号状态")
    private Integer status;
    @ApiModelProperty(name = "remarks", value = "备注")
    private String remarks;
    @ApiModelProperty(name = "userExpireTime", value = "用户过期时间")
    private Date userExpireTime;

    @ApiModelProperty(name = "essStation",value = "用户绑定电站列表")
    private List<EssStation> essStation;

    @ApiModelProperty(name = "userType",value = "用户类型  内部0 外部1")
    private Integer userType;


}
