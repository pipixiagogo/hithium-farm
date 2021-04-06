package com.bugull.hithiumfarmweb.http.bo;

import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateUserBo {
    @NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class})
    @ApiModelProperty(name = "id", required = true, example = "1", value = "用户ID")
    private Integer id;

    @ApiModelProperty(name = "email", required = true, example = " ", value = "用户邮箱")
    private String email;
    @ApiModelProperty(name = "mobile", required = true, example = " ", value = "用户手机号")
    private String mobile;

    @ApiModelProperty(name ="权限ID列表,可不传 代表无任何权限")
    private List<String> roleIds;

}
