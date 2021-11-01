package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateUserBo {
    @NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class})
    private Integer id;

    private String userName;
    @NotBlank(message="邮箱不能为空", groups = { UpdateGroup.class, AddGroup.class})
    @Email(message="邮箱格式不正确", groups = { UpdateGroup.class,AddGroup.class})
    private String email;
    private String mobile;

    private List<String> roleIds;
    private String userExpireTimeStr;
    private String remarks;
    private Integer status;

    private List<String> stationList;

    private Integer userType;


}
