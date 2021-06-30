package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.validator.ValidatorUtils;
import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.hithiumfarmweb.http.bo.PasswordForm;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.service.SysUserService;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户模块
 */
@RestController
@RequestMapping(value = "/user")
public class SysUserController extends AbstractController {

    @Resource
    private SysUserService sysUserService;

    /**
     * TODO 权限
     * @param sysUser
     * @return
     */
    @SysLog(value = "新增用户")
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增用户", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "sysUser", value = "用户实体类", required = true, paramType = "body", dataTypeClass = SysUser.class, dataType = "SysUser")
    public ResHelper<Void> saveUser(@RequestBody SysUser sysUser) {
        /**
         * 非管理员 无法新增用户 做权限管理就好
         * TODO 生成用户时候 未分配权限时候 默认最低权限    直接与menu关联  有权限的的menu都看不到  查的时候根据是否有角色来   生成用户时候分配进去 当分配另一个角色时候 把默认角色移除
         */
        ValidatorUtils.validateEntity(sysUser, AddGroup.class);
        return sysUserService.saveUser(sysUser);
    }

    @GetMapping(value = "/info")
    @ApiOperation(value = "查询本用户登录信息", response = ResHelper.class)
    public ResHelper<InfoUserVo> info() {
        SysUser sysUser = getUser();
        InfoUserVo userVo = new InfoUserVo();
        BeanUtils.copyProperties(sysUser, userVo);
        return ResHelper.success("", userVo);
    }

    /**
     * @param userId
     * @return
     */
    @GetMapping(value = "/info/{userId}")
    @ApiOperation(value = "根据用户ID查询用户信息", response = ResHelper.class)
    @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "path", example = "1", dataType = "int", dataTypeClass = Integer.class)
    public ResHelper<InfoUserVo> infoUserId(@PathVariable String userId) {
        if(StringUtils.isEmpty(userId)){
            return ResHelper.pamIll();
        }
        if(userId.equals(getUserId()) || getUserId().equals(sysUserService.queryAmindId())){
            return ResHelper.success("", sysUserService.infoUserById(userId));
        }
       throw new UnauthorizedException("非管理员无法查看其他用户信息");
    }

    /**
     * TODO 权限
     * @param params
     * @return
     */
    @GetMapping(value = "/listUser")
    @ApiOperation(value = "分页查询用户信息", response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "userName", required = false, paramType = "query", value = "用户名称", dataType = "String", dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<InfoUserVo>> listUser(@ApiIgnore @RequestParam Map<String, Object> params) {
        return sysUserService.list(params);
    }

    @SysLog(value = "修改用户信息")
    @ApiOperation(value = "修改用户信息", response = ResHelper.class)
    @PostMapping(value = "/updateUser")
    @ApiImplicitParam(name = "user", value = "用户修改实体类", required = true, paramType = "body", dataTypeClass = UpdateUserBo.class, dataType = "UpdateUserBo")
    public ResHelper<Void> updateUserById(@RequestBody UpdateUserBo user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        if(String.valueOf(user.getId()).equals(getUserId()) || getUserId().equals(sysUserService.queryAmindId())){
            return sysUserService.updateById(user);
        }
        throw new UnauthorizedException("非管理员无法修改其他用户信息");
    }


    @SysLog(value = "删除用户")
//    @RequiresPermissions("sys:user:delete")
    @ApiOperation(value = "删除用户", response = ResHelper.class)
    @PostMapping(value = "/deleteUser")
    @ApiImplicitParam(name = "userIds", value = "用户ID数组", allowMultiple = true, dataType = "string", required = true)
    public ResHelper<Void> deleteUser(@RequestBody String[] userIds) {
        List<String> users = new ArrayList<>();
        for (String userId : userIds) {
            if (!StringUtils.isEmpty(userId)) {
                users.add(userId);
            }
        }
        if (users.contains(getUserId())) {
            return ResHelper.error("当前用户无法删除");
        }
        if (users.contains(sysUserService.queryAmindId())) {
            return ResHelper.error("管理员账号无法删除");
        }
        return sysUserService.deleteUser(users);
    }

    /**
     * 修改密码
     */
    @ApiOperation(value = "修改密码",response = ResHelper.class)
    @PostMapping(value = "/password")
    @ApiImplicitParam(name = "passwordForm", value = "密码表单实体类", dataType = "PasswordForm",dataTypeClass = PasswordForm.class,required = true)
    public ResHelper<Void> password(@RequestBody PasswordForm passwordForm){
        ValidatorUtils.validateEntity(passwordForm, UpdateGroup.class);
        return sysUserService.password(passwordForm,getUser());
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "退出登录",response = ResHelper.class)
    public ResHelper<Void> logout(){
        return sysUserService.logout(getUser());
    }


    /**
     * 重置密码接口
     * TODO 权限
     */
    @ApiOperation(value = "批量重置密码",response = ResHelper.class)
    @ApiImplicitParam(name = "/userIds", value = "用户ID数组", allowMultiple = true, dataType = "string", required = true)
    @PostMapping(value = "/resetPwd")
    public ResHelper<Void> resetPwd(@RequestBody String[] userIds){
        List<String> users = new ArrayList<>();
        for (String userId : userIds) {
            if (!StringUtils.isEmpty(userId)) {
                users.add(userId);
            }
        }
        return sysUserService.resetPwd(users);
    }
}
