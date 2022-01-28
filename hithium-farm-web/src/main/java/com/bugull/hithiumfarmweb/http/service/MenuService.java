package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.vo.MenuVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        List<MenuEntity> menuEntities = menuEntityDao.findMenu(new Query());
        return ResHelper.success("", menuEntities);
    }

    public List<MenuEntity> getUserMenuList(SysUser sysUser) {
        List<MenuEntity> menuEntityList;
        if (sysUser.getId() == 1L) {
            menuEntityList = menuEntityDao.findMenu(new Query());
        } else {
            /**
             * 非管理员
             */
            List<String> roleIds = sysUser.getRoleIds();
            if (!CollectionUtils.isEmpty(roleIds) && !roleIds.isEmpty()) {

                List<RoleEntity> roleEntityList = roleIds.stream().map(role -> roleEntityDao.findRole(new Query().addCriteria(Criteria.where("id").is(role)))).collect(Collectors.toList());
                List<MenuEntity> menuEntities = new ArrayList<>();
                roleEntityList.stream().forEach(role -> {

                    List<MenuEntity> menus = menuEntityDao.findMenu(new Query().addCriteria(Criteria.where("id").in(role.getMenuIdList())));
                    menuEntities.addAll(menus);
                });
                menuEntityList = menuEntities.stream().distinct().collect(Collectors.toList());
            } else {
                /**
                 * 生成用户时候未指定角色
                 */
                menuEntityList = menuEntityDao.findMenu(new Query().addCriteria(Criteria.where("perms").exists(false)));
            }
        }
        return menuEntityList;
    }

    public List<MenuVo> copyProperties(List<MenuEntity> userMenuList) {
        if (!CollectionUtils.isEmpty(userMenuList) && !userMenuList.isEmpty()) {
            List<MenuVo> menuVoLs = userMenuList.stream().filter(menuEntity -> menuEntity.getType() == 0).map(typeMenu -> {
                MenuVo vo = new MenuVo();
                BeanUtils.copyProperties(typeMenu, vo);
                return vo;
            }).sorted(Comparator.comparing(MenuVo::getOrderNum)).collect(Collectors.toList());
            menuVoLs.forEach(vo -> {
                List<MenuVo> voList = userMenuList.stream().filter(menuEntity -> vo.getId().equals(menuEntity.getParentId().toString())).map(menuEntity -> {
                    MenuVo menu = new MenuVo();
                    BeanUtils.copyProperties(menuEntity, menu);
                    return menu;
                }).sorted(Comparator.comparing(MenuVo::getOrderNum)).collect(Collectors.toList());
                vo.setMenuVoList(voList);
                voList.forEach(menuVo -> {
                    List<MenuVo> menuVoList = userMenuList.stream().filter(menu -> menuVo.getId().equals(menu.getParentId().toString())).map(menuEntity -> {
                        MenuVo menu = new MenuVo();
                        BeanUtils.copyProperties(menuEntity, menu);
                        return menu;
                    }).collect(Collectors.toList());
                    menuVo.setMenuVoList(menuVoList);
                });
            });
            return menuVoLs;
        }
        return new ArrayList<>();
    }

    public ResHelper<List<MenuVo>> selectByLevel() {
        List<MenuEntity> menuEntities = menuEntityDao.findMenu(new Query());
        List<MenuVo> menuVos = copyProperties(menuEntities);
        return ResHelper.success("", menuVos);
    }
}
