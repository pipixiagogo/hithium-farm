package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiroService {


    @Resource
    @Lazy
    private SysUserDao sysUserDao;
    @Lazy
    @Resource
    private MenuEntityDao menuEntityDao;
    @Resource
    @Lazy
    private RoleEntityDao roleEntityDao;

    public Set<String> getPermissions(SysUser sysUser) {
        /**
         * 管理员
         */
        List<MenuEntity> menuEntityList;
        if (Integer.valueOf(sysUser.getId()) == 1) {
            menuEntityList = menuEntityDao.query().results();
        } else {
            /**
             * 非管理员
             */
            SysUser userById = sysUserDao.query().is("_id", sysUser.getId()).is("userName", sysUser.getUserName()).result();
            List<String> roleIds = userById.getRoleIds();
            if (roleIds != null && roleIds.size() > 0) {
                List<RoleEntity> roleEntityList = roleIds.stream().map(role -> {
                    return roleEntityDao.query().is("_id", role).result();
                }).collect(Collectors.toList());
                List<MenuEntity> menuEntities = new ArrayList<>();
                roleEntityList.stream().forEach(role -> {
                    List<MenuEntity> menus = menuEntityDao.query().in("_id", role.getMenuIdList()).results();
                    menuEntities.addAll(menus);
                });
                /**
                 * 根据角色去重
                 */
                menuEntityList = menuEntities.stream().distinct().collect(Collectors.toList());
            }else {
                /**
                 * 生成用户时候未指定角色
                 */
                menuEntityList = new ArrayList<>();
            }

        }
        Set<String> permsSet = new HashSet<>();

        if (menuEntityList.size() > 0 && menuEntityList != null) {
            for (MenuEntity perms : menuEntityList) {
                if (StringUtils.isBlank(perms.getPerms())) {
                    continue;
                }
                permsSet.addAll(Arrays.asList(perms.getPerms().split(",")));
            }
        }
        return permsSet;
    }

    public SysUser queryByToken(String accessToken) {
        return sysUserDao.query().is("token", accessToken).result();
    }
}
