package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.vo.MenuVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Resource
    private MenuEntityDao menuEntityDao;
    @Resource
    private RoleEntityDao roleEntityDao;


    public ResHelper<List<MenuEntity>> select() {
        List<MenuEntity> menuEntities = menuEntityDao.query().results();
        return ResHelper.success("", menuEntities);
    }

    public List<MenuEntity> getUserMenuList(SysUser sysUser) {
        List<MenuEntity> menuEntityList;
        if (Integer.valueOf(sysUser.getId()) == 1) {
            menuEntityList = menuEntityDao.query().results();
        } else {
            /**
             * 非管理员
             */
            List<String> roleIds = sysUser.getRoleIds();
            if (roleIds != null && roleIds.size() > 0) {
                List<RoleEntity> roleEntityList = roleIds.stream().map(role -> {
                    return roleEntityDao.query().is("_id", role).result();
                }).collect(Collectors.toList());
                List<MenuEntity> menuEntities = new ArrayList<>();
                roleEntityList.stream().forEach(role -> {
                    List<MenuEntity> menus = menuEntityDao.query().in("_id", role.getMenuIdList()).results();
                    menuEntities.addAll(menus);
                });
                menuEntityList = menuEntities.stream().distinct().collect(Collectors.toList());
            } else {
                /**
                 * 生成用户时候未指定角色
                 */
                menuEntityList = menuEntityDao.query().notExistsField("perms").results();
            }
        }
        return menuEntityList;
    }

    public List<MenuVo> copyProperties(List<MenuEntity> userMenuList) {
        if (!CollectionUtils.isEmpty(userMenuList) && userMenuList.size() > 0) {
            List<MenuVo> menuVoLs = userMenuList.stream().filter(menuEntity -> {
                return menuEntity.getType() == 0;
            }).map(typeMenu -> {
                MenuVo vo = new MenuVo();
                BeanUtils.copyProperties(typeMenu, vo);
                return vo;
            }).sorted(Comparator.comparing(MenuVo::getOrderNum)).collect(Collectors.toList());
            menuVoLs.forEach(vo -> {
                List<MenuVo> voList = userMenuList.stream().filter(menuEntity -> {
                    return vo.getId().equals(menuEntity.getParentId().toString());
                }).map(menuEntity -> {
                    MenuVo menu = new MenuVo();
                    BeanUtils.copyProperties(menuEntity, menu);
                    return menu;
                }).sorted(Comparator.comparing(MenuVo::getOrderNum)).collect(Collectors.toList());
                vo.setMenuVoList(voList);
                voList.forEach(menuVo -> {
                    List<MenuVo> menuVoList = userMenuList.stream().filter(menu -> {
                        return menuVo.getId().equals(menu.getParentId().toString());
                    }).map(menuEntity -> {
                        MenuVo menu = new MenuVo();
                        BeanUtils.copyProperties(menuEntity, menu);
                        return menu;
                    }).collect(Collectors.toList());
                    menuVo.setMenuVoList(menuVoList);
                });
            });
            return menuVoLs;
        }
        return null;
    }

    public ResHelper<List<MenuVo>> selectByLevel() {
        List<MenuEntity> menuEntities = menuEntityDao.query().results();
        List<MenuVo> menuVos=copyProperties(menuEntities);
        return ResHelper.success("",menuVos);
    }
}
