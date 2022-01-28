package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.service.MenuService;
import com.bugull.hithiumfarmweb.http.service.ShiroService;
import com.bugull.hithiumfarmweb.http.vo.MenuVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 菜单模块
 */
@RequestMapping(value = "/menu")
@RestController
public class MenuController extends AbstractController {

    @Resource
    private MenuService menuService;
    @Resource
    @Lazy
    private ShiroService shiroService;

    @GetMapping(value = "/select")
    public ResHelper<List<MenuEntity>> select() {
        return menuService.select();
    }

    @GetMapping(value = "/selectByLevel")
    public ResHelper<List<MenuVo>> selectByLevel() {
        return menuService.selectByLevel();
    }

    /**
     * 登录后 返回有用户权限的菜单列表
     */
    @GetMapping(value = "/nav")
    public ResHelper<Map<String, Object>> nav() {
        List<MenuEntity> menuEntities = menuService.getUserMenuList(getUser());
        Set<String> permissions = shiroService.getPermissionOfUser(getUser());
        Map<String, Object> map = new HashMap<>();
        map.put("menuList", menuEntities);
        map.put("permissions", permissions);
        return ResHelper.success("", map);
    }

    @GetMapping(value = "/selectByUser")
    public ResHelper<List<MenuVo>> selectByUser() {
        List<MenuEntity> userMenuList = menuService.getUserMenuList(getUser());
        List<MenuVo> menuVos = menuService.copyProperties(userMenuList);
        return ResHelper.success("", menuVos);
    }


}
