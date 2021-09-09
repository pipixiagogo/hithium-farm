package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.annotations.Ignore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateUserBo {
    @NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class})
    @ApiModelProperty(name = "id", required = true, example = "1", value = "用户ID")
    private Integer id;

    @ApiModelProperty(name = "userName", value = "用户名称")
    private String userName;
    @NotBlank(message="邮箱不能为空", groups = { UpdateGroup.class, AddGroup.class})
    @Email(message="邮箱格式不正确", groups = { UpdateGroup.class,AddGroup.class})
    @ApiModelProperty(name = "email", required = true, example = " ", value = "用户邮箱")
    private String email;
    @ApiModelProperty(name = "mobile", required = true, example = " ", value = "手机号")
    private String mobile;

    @ApiModelProperty(name = "roleIds", value = "权限ID列表,可不传 代表无任何权限")
    private List<String> roleIds;
    @ApiModelProperty(name = "userExpireTime", value = "账号过期时间 yyyy-MM-dd")
    private String userExpireTimeStr;
    @ApiModelProperty(name = "remarks", value = "备注")
    private String remarks;
    @ApiModelProperty(name = "status", value = "账号状态")
    private Integer status;

    @ApiModelProperty(name = "stationList",value = "用户绑定设备列表")
    private List<String> stationList;

    @ApiModelProperty(name = "userType",value = "用户类型 内部用户:0 外部用户:1")
    private Integer userType;


}
