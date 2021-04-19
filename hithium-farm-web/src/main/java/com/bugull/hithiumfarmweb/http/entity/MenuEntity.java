package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
public class MenuEntity implements BuguEntity {

    @Id(type = IdType.AUTO_INCREASE)
    private String id;
    //菜单名称
    @ApiModelProperty(value = "菜单名称")
    private String name;
    //    菜单URL
    @ApiModelProperty(value = "菜单url")
    private String url;
    //授权(多个用逗号分隔，如：user:list,user:create)
    @ApiModelProperty(value = "授权信息")
    private String perms;
    //类型 0：目录 1：菜单 2：按钮
    @ApiModelProperty(value = "类型 0：目录 1：菜单 2：按钮")
    private Integer type;
    //菜单图标
    private String icon;
    @ApiModelProperty(value = "排序")
    //排序
    private Integer orderNum;

    private Long menuId;

    //父菜单名称
    @ApiModelProperty(value = "父菜单名称")
    private String parentMenuName;

    private Long parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MenuEntity menus = (MenuEntity) o;
        return Objects.equals(name, menus.name) && menuId==menus.menuId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, menuId);
    }
}
