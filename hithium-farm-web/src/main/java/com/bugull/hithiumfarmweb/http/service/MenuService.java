package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.User;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    public List<MenuEntity> getUserMenuList(User user) {
        List<MenuEntity> menuEntityList;
        if (user.getUserName().equals("admin") && Integer.valueOf(user.getId()) == 1) {
            menuEntityList = menuEntityDao.query().results();
        } else {
            /**
             * 非管理员
             */
            List<String> roleIds = user.getRoleIds();
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
}
