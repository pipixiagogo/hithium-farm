package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuVo {

    private String id;
    private String name;

    private String url;
    private String perms;
    private Integer type;
    private String icon;
    private Integer orderNum;
    private String parentMenuName;
    private Long parentId;

    private List<MenuVo> menuVoList;
}
