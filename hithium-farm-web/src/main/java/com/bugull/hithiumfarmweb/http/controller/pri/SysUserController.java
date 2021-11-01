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
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户模块
 */
@RestController
@RequestMapping(value = "/user")
public class SysUserController extends AbstractController {


    @Resource
    private SysUserService sysUserService;

    /**
     * @param sysUser
     * @return
     */
    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "新增用户")
    @PostMapping(value = "/save")
    public ResHelper<Void> saveUser(@RequestBody SysUser sysUser) {
        ValidatorUtils.validateEntity(sysUser, AddGroup.class);
        return sysUserService.saveUser(sysUser);
    }

    @GetMapping(value = "/info")
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
    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/info/{userId}")
    public ResHelper<InfoUserVo> infoUserId(@PathVariable String userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResHelper.pamIll();
        }
        if (userId.equals(getUserId()) || getUserId().equals(sysUserService.queryAmindId())) {
            return ResHelper.success("", sysUserService.infoUserById(userId));
        }
        throw new UnauthorizedException("非管理员无法查看其他用户信息");
    }

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/listUser")
    public ResHelper<BuguPageQuery.Page<InfoUserVo>> listUser(@RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                              @RequestParam(value = "userName", required = false) String userName) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return sysUserService.list(page, pageSize, userName);
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "修改用户信息")
    @PostMapping(value = "/updateUser")
    public ResHelper<Void> updateUserById(@RequestBody UpdateUserBo user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        if (String.valueOf(user.getId()).equals(getUserId()) || getUserId().equals(sysUserService.queryAmindId())) {
            return sysUserService.updateById(user);
        }
        throw new UnauthorizedException("非管理员无法修改其他用户信息");
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "删除用户")
    @PostMapping(value = "/deleteUser")
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
     * 重置密码接口
     */
    @RequiresPermissions(value = "sys:user")
    @PostMapping(value = "/resetPwd")
    @SysLog(value = "批量重置密码")
    public ResHelper<Void> resetPwd(@RequestBody String[] userIds) {
        List<String> users = new ArrayList<>();
        for (String userId : userIds) {
            if (!StringUtils.isEmpty(userId)) {
                users.add(userId);
            }
        }
        return sysUserService.resetPwd(users);
    }

    /**
     * 修改密码
     */
    @SysLog(value = "修改密码")
    @PostMapping(value = "/password")
    public ResHelper<Void> password(@RequestBody PasswordForm passwordForm) {
        ValidatorUtils.validateEntity(passwordForm, UpdateGroup.class);
        return sysUserService.password(passwordForm, getUser());
    }

    @PostMapping(value = "/logout")
    public ResHelper<Void> logout() {
        return sysUserService.logout(getUser());
    }


}
