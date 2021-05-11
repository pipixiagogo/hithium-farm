package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.validator.ValidatorUtils;
import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.hithiumfarmweb.http.bo.RoleEntityBo;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.service.RoleService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @ApiOperation(value = "查询角色列表",response = ResHelper.class)
    @GetMapping(value = "/selectRole")
    public ResHelper<List<RoleEntity>> selectRole() {
        return  roleService.select(getUser());
    }

    /**
     * 分页查询角色
     * @return
     */
    @GetMapping(value = "/queryPageRole")
    @ApiOperation(value = "分页查询角色信息",response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1",name = "page", value = "当前页码",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10",name = "pageSize", value = "每页记录数",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "roleName", required = false,paramType = "query",value = "角色名称",dataType = "String",dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<RoleEntity>> queryPageRole(@ApiIgnore @RequestParam Map<String, Object> params){
        return roleService.query(params);
    }

    /**
     * 添加角色
     */
    @SysLog(value = "新增角色")
    @PostMapping(value = "/saveRole")
    @ApiOperation(value = "新增角色",response = ResHelper.class,httpMethod = "POST")
    @ApiImplicitParam(name = "roleEntityBo", value = "角色添加实体类",required = true,paramType = "body",dataTypeClass = RoleEntityBo.class, dataType = "RoleEntityBo")
    public ResHelper<Void> saveRole(@RequestBody RoleEntityBo roleEntityBo){
        /**
         * 非管理员 无法新增用户
         */
        ValidatorUtils.validateEntity(roleEntityBo, AddGroup.class);
        roleEntityBo.setCreateId(getUserId());
        roleEntityBo.setCreateName(getUserName());
        return roleService.saveRole(roleEntityBo);
    }

    @SysLog(value = "修改角色信息")
    @ApiOperation(value = "修改角色信息",response = ResHelper.class)
    @PostMapping(value = "/updateRole")
    @ApiImplicitParam(name = "roleEntity",value = "角色修改实体类",required = true,paramType = "body",dataTypeClass = RoleEntity.class,dataType = "RoleEntity")
    public ResHelper<Void> updateRoleById(@RequestBody RoleEntity roleEntity){
        ValidatorUtils.validateEntity(roleEntity, UpdateGroup.class);
        roleEntity.setUpdateTime(new Date());
        roleEntity.setUpdateUserName(getUserName());
        roleEntity.setUpdateId(getUserId());
        return  roleService.updateRole(roleEntity);
    }

    /**
     *
     * @param roleIds
     * @return
     */
    @SysLog(value = "批量/单条删除角色")
    @ApiOperation(value = "批量/单条删除角色",response = ResHelper.class)
    @PostMapping(value = "/deleteRole")
    @ApiImplicitParam(name = "roleIds",value = "角色ID数组",allowMultiple = true,dataType = "string",required = true)
    public ResHelper<Void> deleteRole(@RequestBody String[] roleIds ){
        List<String> roIds=new ArrayList<>();
        for(String userId:roleIds){
            if(!StringUtils.isEmpty(userId)){
                roIds.add(userId);
            }
        }
        return roleService.deleteRoleByIds(roIds);
    }


}
