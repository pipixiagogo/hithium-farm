package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class RoleEntityBo {
    //角色名称
    @NotBlank(message = "角色名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(name = "roleName", required = true, example = "RI", value = "角色名称")
    private String roleName;
    //备注
    @ApiModelProperty(name = "remark", required = true, example = " ", value = "备注")
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


    //接受前端参数  menuIds
    @NotEmpty(message = "菜单栏ID为空", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(name = "menuIdList", required = true, example = "[1,2,3,4]", value = "菜单栏ID")
    private List<String> menuIdList;
}
