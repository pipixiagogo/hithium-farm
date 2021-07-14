package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.RoleEntityBo;
import com.bugull.hithiumfarmweb.http.dao.MenuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService {

    @Resource
    private RoleEntityDao roleEntityDao;
    @Resource
    private SysUserDao sysUserDao;

    public ResHelper<List<RoleEntity>> select(SysUser sysUser) {
        if (Integer.valueOf(sysUser.getId()) == 1) {
            List<RoleEntity> results = roleEntityDao.query().results();
            return ResHelper.success("", results);
        }
        if(!CollectionUtils.isEmpty(sysUser.getRoleIds()) && !sysUser.getRoleIds().isEmpty()){
            List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", sysUser.getRoleIds()).results();
            return ResHelper.success("",roleEntities);
        }
        return null;
    }

    public ResHelper<BuguPageQuery.Page<RoleEntity>> query(Map<String, Object> params) {
        BuguPageQuery<RoleEntity> query =  roleEntityDao.pageQuery();
        String roleName = (String) params.get("roleName");
        if (!StringUtils.isBlank(roleName)) {
            query.regexCaseInsensitive("roleName", roleName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<RoleEntity> roleEntityPage = query.resultsWithPage();
        return ResHelper.success("", roleEntityPage);
    }

    public ResHelper<Void> saveRole(RoleEntityBo roleEntityBo) {
        if(roleEntityDao.query().is("roleName",roleEntityBo.getRoleName()).exists()){
            return ResHelper.error("角色名称已存在");
        }
        roleEntityBo.setCreateTime(new Date());
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleEntityBo, roleEntity);
        roleEntityDao.insert(roleEntity);
        return ResHelper.success("添加角色成功");
    }

    public ResHelper<Void> updateRole(RoleEntity roleEntity) {
        BuguUpdater<RoleEntity> update = roleEntityDao.update();
        if (roleEntityDao.query().is("roleName", roleEntity.getRoleName()).notEquals("_id", roleEntity.getId()).exists()) {
            return ResHelper.error("角色名称已存在");
        }
        update.set("roleName", roleEntity.getRoleName())
                .set("remark", roleEntity.getRemark())
                .set("updateTime", roleEntity.getUpdateTime())
                .set("updateUserName", roleEntity.getUpdateUserName())
                .set("menuIdList", roleEntity.getMenuIdList())
                .set("updateId", roleEntity.getUpdateId())
                .execute(roleEntityDao.query().is("_id", String.valueOf(roleEntity.getId())));
        return ResHelper.success("修改角色成功");
    }

    public ResHelper<Void> deleteRoleByIds(List<String> roIds) {
        try {
            roleEntityDao.remove(roIds);
            /**
             * 删除user列表下的角色
             */
            List<SysUser> userList = sysUserDao.query().in("roleIds", roIds).results();
            if (!CollectionUtils.isEmpty(userList) && !userList.isEmpty()) {
                Set<String> set = new HashSet<>(roIds);
                userList.stream().forEach(user -> {
                    Set<String> userRoleSet = new HashSet<>(user.getRoleIds());
                    userRoleSet.removeAll(set);
                    sysUserDao.update().set("roleIds", userRoleSet).execute(user);
                });
            }
            return ResHelper.success("删除角色成功");
        }catch (Exception e){
            return ResHelper.error("删除角色失败");
        }


    }
}
