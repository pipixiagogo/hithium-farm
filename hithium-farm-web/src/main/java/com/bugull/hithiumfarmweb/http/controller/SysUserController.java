package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.Page;
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
        if (sysUser == null || StringUtils.isEmpty(sysUser.getUserName())) {
            return ResHelper.error("用户名不能为空");
        }
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
        if (Long.valueOf(userId) == getUserId() || getUserId() == sysUserService.queryAdminId()) {
            return ResHelper.success("", sysUserService.infoUserById(Long.valueOf(userId)));
        }
        throw new UnauthorizedException("非管理员无法查看其他用户信息");
    }

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/listUser")
    public ResHelper<Page<InfoUserVo>> listUser(@RequestParam(value = "page", required = false) Integer page,
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
        if (user.getId() == null) {
            return ResHelper.pamIll();
        }
        if (user.getId() == getUserId() || getUserId()== sysUserService.queryAdminId()) {
            return sysUserService.updateById(user);
        }
        throw new UnauthorizedException("非管理员无法修改其他用户信息");
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "删除用户")
    @PostMapping(value = "/deleteUser")
    public ResHelper<Void> deleteUser(@RequestBody String [] userIds) {
        List<String> users = new ArrayList<>();
        for (String userId : userIds) {
            if (!StringUtils.isEmpty(userId)) {
                users.add(userId);
            }
        }
        if (users.contains(getUserId())) {
            return ResHelper.error("当前用户无法删除");
        }
        if (users.contains(sysUserService.queryAdminId())) {
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
    public ResHelper<Void> resetPwd(@RequestBody String [] userIds) {
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
        if (passwordForm == null || StringUtils.isEmpty(passwordForm.getPassword()) || StringUtils.isEmpty(passwordForm.getNewPassword())) {
            return ResHelper.error("新旧密码错误");
        }
        return sysUserService.password(passwordForm, getUser());
    }

    @PostMapping(value = "/logout")
    public ResHelper<Void> logout() {
        return sysUserService.logout(getUser());
    }


}
