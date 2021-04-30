package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@Entity
@ApiModel
public class RoleEntity implements BuguEntity {
    @Id(type = IdType.AUTO_GENERATE)
    @NotBlank(message = "角色ID不能为空",groups = {UpdateGroup.class})
    @ApiModelProperty(name = "id", required = true, example = " ", value = "角色ID")
    private String id;
    //角色名称
    @NotBlank(message = "角色名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(name = "roleName", required = true, example = "RI", value = "角色名称")
    private String roleName;
    //备注
    @ApiModelProperty(name = "remark", required = false, example = " ", value = "备注")
    private String remark;
    //创建者的ID
    @ApiModelProperty(hidden = true)
    private String createId;
    //创建者的名称
    @ApiModelProperty(hidden = true)
    private String createName;
    //创建时间
    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @ApiModelProperty(hidden = true)
    private String updateUserName;

    @ApiModelProperty(hidden = true)
    private String updateId;

    //接受前端参数  menuIds
    @NotEmpty(message = "菜单栏ID为空", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(name = "menuIdList", required = true, example = "[1,2,3,4]", value = "菜单栏ID")
    private List<String> menuIdList;
}
