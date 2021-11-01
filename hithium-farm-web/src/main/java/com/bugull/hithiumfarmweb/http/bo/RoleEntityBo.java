package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class RoleEntityBo {
    //角色名称
    @NotBlank(message = "角色名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String roleName;
    //备注
    private String remark;
    //创建者的ID
    private String createId;
    //创建者的名称
    private String createName;
    //创建时间
    private Date createTime;


    //接受前端参数  menuIds
    @NotEmpty(message = "菜单栏ID为空", groups = {AddGroup.class, UpdateGroup.class})
    private List<String> menuIdList;
}
