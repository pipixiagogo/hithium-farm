package com.bugull.hithiumfarmweb.http.entity;


import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.omg.CORBA.PRIVATE_MEMBER;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@ToString
@ApiModel
@Entity
public class User implements BuguEntity {

    @ApiModelProperty(hidden = true)
    @Id(type = IdType.AUTO_INCREASE,start = 1L)
    private String id;

    @ApiModelProperty(name = "userName", required = true, example = "test   ", value = "登录用户名")
    @NotBlank(message = "用户名称不能为空", groups = {UpdateGroup.class,AddGroup.class})
    private String userName;

    @ApiModelProperty(name = "password", required = true, example = "123456", value = "用户密码")
    @NotBlank(message = "用户密码不能为空", groups = {UpdateGroup.class,AddGroup.class})
    private String password;
    @ApiModelProperty(hidden = true)
    private String salt;

    private String mobile;
    @NotBlank(message="邮箱不能为空", groups = { UpdateGroup.class, AddGroup.class})
    @Email(message="邮箱格式不正确", groups = { UpdateGroup.class,AddGroup.class})
    @ApiModelProperty(name = "email",value = "邮箱")
    private String email;

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



}
