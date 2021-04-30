package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.bo.PasswordForm;
import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.dao.CaptchaDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.oauth2.TokenGenerator;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.RESET_PWD;

@Service
public class SysUserService {
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private RoleEntityDao roleEntityDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private CaptchaDao captchaDao;
    private static final Logger log = LoggerFactory.getLogger(SysUserService.class);


    public ResHelper<Void> saveUser(SysUser sysUser) {
//        if (!ResHelper.validatePhone(sysUser.getMobile())) {
//            return ResHelper.error("手机号格式错误");
//        }
        /**
         * 邮箱+用户名相同  或者 电话 +用户名相同 即为同一用户
         */
        if (sysUserDao.query().is("userName", sysUser.getUserName()).exists()) {
            return ResHelper.error("用户已存在");
        }
//        if (sysUserDao.query().is("email", sysUser.getEmail()).exists() ||
//                sysUserDao.query().is("mobile", sysUser.getMobile()).exists()) {
//            return ResHelper.error("邮箱或手机号已被注册");
//        }
        try {
            if (sysUser.getRoleIds() != null && sysUser.getRoleIds().size() > 0) {
                /**
                 * 验证roleIds的参数
                 */
                List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", sysUser.getRoleIds()).results();
                if (!CollectionUtils.isEmpty(roleEntities) && roleEntities.size() > 0) {

                } else {
                    return ResHelper.error("角色不存在");
                }
            }
            sysUser.setCreateTime(new Date());
            //sha256加密
            String salt = RandomStringUtils.randomAlphanumeric(20);
            sysUser.setPassword(new Sha256Hash(sysUser.getPassword(), salt).toHex());
            sysUser.setSalt(salt);
            sysUserDao.insert(sysUser);
            return ResHelper.success("添加用户成功");
        } catch (Exception e) {
            log.error("查询角色异常", e);
            return ResHelper.error("参数错误");
        }
    }

    public InfoUserVo infoUserById(String userId) {
        SysUser sysUser = sysUserDao.query().is("_id", userId).notReturnFields("salt", "password", "token", "tokenExpireTime").result();
        InfoUserVo infoUserVo = new InfoUserVo();
        if (sysUser != null) {
            BeanUtils.copyProperties(sysUser, infoUserVo);
        }
        return infoUserVo;
    }

    public ResHelper<BuguPageQuery.Page<InfoUserVo>> list(Map<String, Object> params) {
        BuguPageQuery<SysUser> query = (BuguPageQuery<SysUser>) sysUserDao.pageQuery();
        String userName = (String) params.get("userName");
        if (!StringUtils.isBlank(userName)) {
            query.regexCaseInsensitive("userName", userName);
        }
        query.notReturnFields("salt", "password", "token", "tokenExpireTime");
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<SysUser> userResults = query.resultsWithPage();
        List<SysUser> userList = userResults.getDatas();
        List<InfoUserVo> infoUserVos;
        if (userList != null && userList.size() > 0) {
            infoUserVos = userList.stream().map(user -> {
                InfoUserVo infoUserVo = new InfoUserVo();
                List<RoleEntityOfUserBo> roleEntityOfUserBoList = new ArrayList<>();
                if (user.getId().equals("1")) {
                    RoleEntityOfUserBo roleEntity = new RoleEntityOfUserBo();
                    roleEntity.setRoleName("超级管理员");
                    roleEntityOfUserBoList.add(roleEntity);
                    infoUserVo.setRoleEntityLsit(roleEntityOfUserBoList);
                } else {
                    if (!CollectionUtils.isEmpty(user.getRoleIds()) && user.getRoleIds().size() > 0) {
                        List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", user.getRoleIds()).results();
                        BeanUtils.copyProperties(user, infoUserVo);
                        for (RoleEntity roleEntity : roleEntities) {
                            RoleEntityOfUserBo roleEntityOfUserBo = new RoleEntityOfUserBo();
                            BeanUtils.copyProperties(roleEntity, roleEntityOfUserBo);
                            roleEntityOfUserBoList.add(roleEntityOfUserBo);
                        }
                        infoUserVo.setRoleEntityLsit(roleEntityOfUserBoList);
                    }
                }
                BeanUtils.copyProperties(user, infoUserVo);
                return infoUserVo;
            }).collect(Collectors.toList());
        } else {
            infoUserVos = new ArrayList<>();
        }
        BuguPageQuery.Page<InfoUserVo> infoUserVoPage = new BuguPageQuery.Page<>(userResults.getPage(), userResults.getPageSize(), userResults.getTotalRecord(), infoUserVos);
        return ResHelper.success("", infoUserVoPage);
    }

    public ResHelper<Void> updateById(UpdateUserBo user) {

        if (sysUserDao.query().is("_id", String.valueOf(user.getId())).exists()) {
            /**
             * 验证手机号格式
             */
//            if (!ResHelper.validatePhone(user.getMobile())) {
//                return ResHelper.error("手机号格式错误");
//            }
            BuguUpdater<SysUser> update = sysUserDao.update();
//            if (sysUserDao.query().is("email", user.getEmail()).notEquals("_id", String.valueOf(user.getId())).exists() ||
//                    sysUserDao.query().is("mobile", user.getMobile()).notEquals("_id", String.valueOf(user.getId())).exists()) {
//                return ResHelper.error("邮箱/手机号已存在");
//            }
//            if (!StringUtils.isBlank(user.getMobile())) {
//                update.set("mobile", user.getMobile());
//            }
//            if (!StringUtils.isBlank(user.getEmail())) {
//                update.set("email", user.getEmail());
//            }
            if (!StringUtils.isEmpty(user.getUserName())) {
                if (sysUserDao.query().is("userName", user.getUserName()).notEquals("_id", String.valueOf(user.getId())).exists()) {
                    return ResHelper.error("邮箱/手机号已存在");
                } else {
                    update.set("userName", user.getUserName());
                }
            }
            if (user.getRoleIds() != null && user.getRoleIds().size() > 0) {
                List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", user.getRoleIds()).results();
                if (roleEntities != null && roleEntities.size() > 0) {
                    update.set("roleIds", user.getRoleIds());
                } else {
                    return ResHelper.pamIll();
                }
            }
            update.execute(sysUserDao.query().is("_id", String.valueOf(user.getId())));
            return ResHelper.success("修改用户成功");
        } else {
            return ResHelper.error("该用户不存在");
        }
    }


    public ResHelper<LoginVo> loginByPassword(LoginFormBo loginFormBo) {
        SysUser sysUser = sysUserDao.query().is("userName", loginFormBo.getUserName()).result();
        if (sysUser == null || !sysUser.getPassword().equals(new Sha256Hash(loginFormBo.getPassword(), sysUser.getSalt()).toHex())) {
            return ResHelper.error("账号或密码错误");
        }
        if (sysUser.getStatus() != 1) {
            return ResHelper.error("账号被锁定");
        }
        //删除验证码
        captchaDao.remove(captchaDao.query().is("uuid", loginFormBo.getUuid()));
        return ResHelper.success("登录成功", createToken(sysUser.getId()));
    }

    public LoginVo createToken(String id) {
        LoginVo loginVo = new LoginVo();
        //生成一个token
        String token = TokenGenerator.generateValue();
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + propertiesConfig.getTokenExpireTime());
        /**
         * 生成一个refreshToken
         */
        String refreshToken = TokenGenerator.generateValue();
        Date refreshTokenExpireTime = new Date(now.getTime() + 2 * propertiesConfig.getTokenExpireTime());
        sysUserDao.update().set("token", token)
                .set("tokenExpireTime", expireTime)
                .set("createTokenTime", now)
                .set("refreshToken", refreshToken)
                .set("refreshTokenExpireTime", refreshTokenExpireTime)
                .execute(sysUserDao.query().is("_id", id));
        loginVo.setToken(token);
        loginVo.setTokenExpireTime(expireTime);
        loginVo.setRefreshToken(refreshToken);
        loginVo.setRefreshTokenExpireTime(refreshTokenExpireTime);
        return loginVo;
    }

    public ResHelper<Void> deleteUser(List<String> userIds) {
        if (userIds != null && userIds.size() > 0) {
            sysUserDao.remove(userIds);
        }
        return ResHelper.success("删除成功");
    }

    public String queryAmindId() {
        SysUser sysUser = sysUserDao.query().is("_id", "1").result();
        return sysUser.getId();
    }

    public ResHelper<Void> password(PasswordForm passwordForm, SysUser sysUser) {
        //sha256加密
        String password = new Sha256Hash(passwordForm.getPassword(), sysUser.getSalt()).toHex();
        //sha256加密
        SysUser userName = sysUserDao.query().is("userName", sysUser.getUserName()).result();
        if (userName != null && sysUser.getPassword().equals(password)) {
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + propertiesConfig.getTokenExpireTime());
            Date refreshTokenExpireTime = new Date(now.getTime() + 2 * propertiesConfig.getTokenExpireTime());
            /**
             * 修改密码加修改盐
             */
            String salt = RandomStringUtils.randomAlphanumeric(20);
            sysUserDao.update().set("password", new Sha256Hash(passwordForm.getNewPassword(), salt).toHex())
                    .set("salt", salt)
                    .set("token", TokenGenerator.generateValue())
                    .set("tokenExpireTime", expireTime)
                    .set("refreshToken", TokenGenerator.generateValue())
                    .set("refreshTokenExpireTime", refreshTokenExpireTime)
                    .execute(sysUser);
            return ResHelper.success("修改密码成功,请重新登录");
        } else {
            return ResHelper.error("原密码不正确");
        }
    }

    public ResHelper<LoginVo> refreshToken(String refreshToken) {
        if (StringUtils.isEmpty(refreshToken)) {
            return ResHelper.pamIll();
        }
        SysUser result = sysUserDao.query().is("refreshToken", refreshToken).result();
        if (result != null) {
            if (result.getRefreshToken().equals(refreshToken) && !result.getRefreshTokenExpireTime().before(new Date())) {
                return ResHelper.success("刷新token成功", this.createToken(result.getId()));
            }
        }
        return ResHelper.error("刷新token过期,请重新登录");
    }

    public ResHelper<Void> logout(SysUser sysUser) {
        this.createToken(sysUser.getId());
        return ResHelper.success("登出成功");
    }

    public ResHelper<Void> resetPwd(List<String> users) {
        if (users != null && users.size() > 0) {
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + propertiesConfig.getTokenExpireTime());
            Date refreshTokenExpireTime = new Date(now.getTime() + 2 * propertiesConfig.getTokenExpireTime());
            users.forEach(userId -> {
                String salt = RandomStringUtils.randomAlphanumeric(20);
                String resetPwd = new Sha256Hash(RESET_PWD, salt).toHex();
                sysUserDao.update().set("salt", salt)
                        .set("password", resetPwd)
                        .set("token", TokenGenerator.generateValue())
                        .set("tokenExpireTime", expireTime)
                        .set("refreshToken", TokenGenerator.generateValue())
                        .set("refreshTokenExpireTime", refreshTokenExpireTime)
                        .execute(userId);
            });
            return ResHelper.success("重置密码成功");
        }
        return ResHelper.error("");
    }
}