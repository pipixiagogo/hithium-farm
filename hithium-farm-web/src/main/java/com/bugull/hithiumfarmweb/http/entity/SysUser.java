package com.bugull.hithiumfarmweb.http.entity;


import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@ToString
@ApiModel
@Entity
public class SysUser implements BuguEntity {

    @ApiModelProperty(hidden = true)
    @Id(type = IdType.AUTO_INCREASE,start = 1L)
    private String id;

    @ApiModelProperty(name = "userName", required = true, example = "test", value = "登录用户名")
    @NotBlank(message = "用户名称不能为空", groups = {UpdateGroup.class,AddGroup.class})
    private String userName;

    @ApiModelProperty(name = "password", required = true, example = "123456", value = "用户密码")
    @NotBlank(message = "用户密码不能为空", groups = {UpdateGroup.class,AddGroup.class})
    private String password;
    @ApiModelProperty(hidden = true)
    private String salt;

    private String mobile;
//    @NotBlank(message="邮箱不能为空", groups = { UpdateGroup.class, AddGroup.class})
//    @Email(message="邮箱格式不正确", groups = { UpdateGroup.class,AddGroup.class})
//    @ApiModelProperty(name = "email",value = "邮箱")
//    private String email;

    /**
     * 状态  0：禁用   1：正常
     */
    @ApiModelProperty(hidden = true)
    private Integer status=1;
    @ApiModelProperty(hidden = true)
    private String token;
    @ApiModelProperty(hidden = true)
    private Date createTime;
    @ApiModelProperty(hidden = true)
    private Date tokenExpireTime;
    @ApiModelProperty(name ="roleIds",value = "权限ID列表,可不传 代表无任何权限")
    private List<String> roleIds;
    /**
     * 刷新token
     */
    @ApiModelProperty(hidden = true)
    private String refreshToken;
    /**
     * 刷新token过期时间
     */
    @ApiModelProperty(hidden = true)
    private Date refreshTokenExpireTime;
    /**
     * 备注
     */
    @ApiModelProperty(name ="remarks",value = "备注")
    private String remarks;
    /**
     * 用户账号过期时间
     */
    @ApiModelProperty(name ="userExpireTime",value = "账号过期时间 yyyy-MM-dd")
    @Ignore
    private String userExpireTimeStr;
    @ApiModelProperty(hidden = true)
    private Date userExpireTime;

    @ApiModelProperty(name = "stationList",value = "用户绑定电站列表")
    private List<String> stationList;

    @ApiModelProperty(hidden = true)
    private String perms;

    @ApiModelProperty(name = "userType",value = "用户类型 内部用户 0  外部用户 1")
    private Integer userType;
}
