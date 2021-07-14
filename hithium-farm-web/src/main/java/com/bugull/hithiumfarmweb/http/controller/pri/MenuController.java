package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.service.MenuService;
import com.bugull.hithiumfarmweb.http.service.ShiroService;
import com.bugull.hithiumfarmweb.http.vo.MenuVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "查询全部菜单列表", response = ResHelper.class)
    @GetMapping(value = "/select")
    public ResHelper<List<MenuEntity>> select() {
        return menuService.select();
    }

    @ApiOperation(value = "查询全部菜单列表( 带层级 )", response = ResHelper.class)
    @GetMapping(value = "/selectByLevel")
    public ResHelper<List<MenuVo>> selectByLevel() {
        return menuService.selectByLevel();
    }

    /**
     * 登录后 返回有用户权限的菜单列表
     */
    @GetMapping(value = "/nav")
    @ApiOperation("返回当前用户菜单")
    public ResHelper<Map<String, Object>> nav() {
        List<MenuEntity> menuEntities = menuService.getUserMenuList(getUser());
        Set<String> permissions = shiroService.getPermissionOfUser(getUser());
        Map<String, Object> map = new HashMap<>();
        map.put("menuList", menuEntities);
        map.put("permissions", permissions);
        return ResHelper.success("", map);
    }

    @GetMapping(value = "/selectByUser")
    @ApiOperation("返回当前用户菜单列表(带层级关系)")
    public ResHelper<List<MenuVo>> selectByUser() {
        List<MenuEntity> userMenuList = menuService.getUserMenuList(getUser());
        List<MenuVo> menuVos = menuService.copyProperties(userMenuList);
        return ResHelper.success("", menuVos);
    }


}
