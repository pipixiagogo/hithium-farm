package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.service.RoleService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO 权限
 * 角色模块
 */
@RestController
@RequestMapping(value = "/role")
public class RoleController extends AbstractController {

    @Resource
    private RoleService roleService;

    /**
     * 查询全部角色
     * @return
     */
    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/selectRole")
    public ResHelper<List<RoleEntity>> selectRole() {
        return  roleService.select(getUser());
    }

    /**
     * 分页查询角色
     * @return
     */
//    @RequiresPermissions(value = "sys:user")
//    @GetMapping(value = "/queryPageRole")
//    public ResHelper<Page<RoleEntity>> queryPageRole(@RequestParam Map<String, Object> params){
//        return roleService.query(params);
//    }

    /**
     * 添加角色
     */
//    @RequiresPermissions(value = "sys:user")
//    @SysLog(value = "新增角色")
//    @PostMapping(value = "/saveRole")
//    public ResHelper<Void> saveRole(@RequestBody RoleEntityBo roleEntityBo){
//        /**
//         * 非管理员 无法新增用户
//         */
//        roleEntityBo.setCreateId(getUserId());
//        roleEntityBo.setCreateName(getUserName());
//        return roleService.saveRole(roleEntityBo);
//    }
//    @RequiresPermissions(value = "sys:user")
//    @SysLog(value = "修改角色信息")
//    @PostMapping(value = "/updateRole")
//    public ResHelper<Void> updateRoleById(@RequestBody RoleEntity roleEntity){
//        roleEntity.setUpdateTime(new Date());
//        roleEntity.setUpdateUserName(getUserName());
//        roleEntity.setUpdateId(getUserId());
//        return  roleService.updateRole(roleEntity);
//    }

    /**
     * @param roleIds
     * @return
     */
//    @RequiresPermissions(value = "sys:user")
//    @SysLog(value = "批量/单条删除角色")
//    @PostMapping(value = "/deleteRole")
//    public ResHelper<Void> deleteRole(@RequestBody String[] roleIds ){
//        List<String> roIds=new ArrayList<>();
//        for(String userId:roleIds){
//            if(!StringUtils.isEmpty(userId)){
//                roIds.add(userId);
//            }
//        }
//        return roleService.deleteRoleByIds(roIds);
//    }


}
