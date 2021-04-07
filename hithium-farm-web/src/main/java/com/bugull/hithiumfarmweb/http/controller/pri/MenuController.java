package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.service.MenuService;
import com.bugull.hithiumfarmweb.http.service.ShiroService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    private ShiroService shiroService;

    @ApiOperation(value = "查询菜单列表", response = ResHelper.class)
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public ResHelper<List<MenuEntity>> select() {
        return menuService.select();
    }

    /**
     * 登录后 返回有用户权限的菜单列表
     */
    @RequestMapping(value = "/nav",method = RequestMethod.GET)
    @ApiOperation("返回当前用户菜单")
    public ResHelper<Map<String, Object>> nav() {
        List<MenuEntity> menuEntities = menuService.getUserMenuList(getUser());
        Set<String> permissions = shiroService.getPermissions(getUser());
        Map<String, Object> map = new HashMap<>();
        map.put("menuList", menuEntities);
        map.put("permissions", permissions);
        return ResHelper.success("", map);
    }


}