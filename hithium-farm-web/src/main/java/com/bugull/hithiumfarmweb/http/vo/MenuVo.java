package com.bugull.hithiumfarmweb.http.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
public class MenuVo {

    private String id;
    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "菜单URL")
    private String url;
    @ApiModelProperty(value = "授权 多个用逗号分隔，如：user:list,user:create")
    private String perms;
    @ApiModelProperty(value = "类型 0：目录 1：菜单 2：按钮")
    private Integer type;
    @ApiModelProperty(value = "菜单图标")
    private String icon;
    @ApiModelProperty(value = "排序")
    private Integer orderNum;
    @ApiModelProperty(value ="父菜单名称")
    private String parentMenuName;
    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "子菜单信息")
    private List<MenuVo> menuVoList;
}
