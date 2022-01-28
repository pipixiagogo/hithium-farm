package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class MenuEntity  {

    private String id;
    //菜单名称
    private String name;
    //    菜单URL
    private String url;
    //授权(多个用逗号分隔，如：user:list,user:create)
    private String perms;
    //类型 0：目录 1：菜单 2：按钮
    private Integer type;
    //菜单图标
    private String icon;
    //排序
    private Integer orderNum;

    private Long menuId;

    //父菜单名称
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
