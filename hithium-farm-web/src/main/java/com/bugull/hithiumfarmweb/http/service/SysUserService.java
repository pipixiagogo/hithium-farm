package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.bo.PasswordForm;
import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.dao.EssStationDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.oauth2.TokenGenerator;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.PASSWORD_PATTERN;
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
    private EssStationDao essStationDao;
    private static final Logger log = LoggerFactory.getLogger(SysUserService.class);


    public ResHelper<Void> saveUser(SysUser sysUser) {
        try {
            if (!sysUser.getPassword().matches(PASSWORD_PATTERN)) {
                return ResHelper.error("请输入英文与数字组合的6至8位数密码!");
            }
            if (StringUtils.isEmpty(sysUser.getMobile()) || !PatternUtil.isMobile(sysUser.getMobile())) {
                return ResHelper.error("手机号码格式错误");
            }
            if (!StringUtils.isEmpty(sysUser.getUserExpireTimeStr())) {
                if (!Const.DATE_PATTERN.matcher(sysUser.getUserExpireTimeStr()).matches()) {
                    return ResHelper.error("账号过期时间:日期格式错误");
                }
                Date userExpireTime = DateUtils.getEndTime(DateUtils.dateToStrWithHHmm(sysUser.getUserExpireTimeStr()));
                sysUser.setUserExpireTime(userExpireTime);
            }
            /**
             * 邮箱+用户名相同  或者 电话 +用户名相同 即为同一用户
             */
            if (sysUserDao.query().is("userName", sysUser.getUserName()).exists() || sysUserDao.query().is("mobile", sysUser.getMobile()).exists()
                    || sysUserDao.query().is("email", sysUser.getEmail()).exists()) {
                return ResHelper.error("用户名/手机号/邮箱已存在");
            }
            if (!CollectionUtils.isEmpty(sysUser.getStationList()) && !sysUser.getStationList().isEmpty()) {
                for (String stationId : sysUser.getStationList()) {
                    if (!essStationDao.exists(stationId)) {
                        return ResHelper.pamIll();
                    }
                }
            }
            if (!CollectionUtils.isEmpty(sysUser.getRoleIds()) && !sysUser.getRoleIds().isEmpty()) {
                /**
                 * 验证roleIds的参数
                 */
                List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", sysUser.getRoleIds()).results();
                if (!CollectionUtils.isEmpty(roleEntities) && !roleEntities.isEmpty()) {
                } else {
                    return ResHelper.error("角色不存在");
                }
            }
            if (sysUser.getUserType() == null) {
                return ResHelper.pamIll();
            }
            if (sysUser.getUserType() == 0 || sysUser.getUserType() == 1) {
                sysUser.setPerms("sys:device");
            }
            sysUser.setCreateTime(new Date());
            //sha256加密
            String salt = RandomStringUtils.randomAlphanumeric(20);
            sysUser.setPassword(new Sha256Hash(sysUser.getPassword(), salt).toHex());
            sysUser.setSalt(salt);
            sysUserDao.insert(sysUser);
            return ResHelper.success("添加用户成功");
        } catch (Exception e) {
            log.error("添加角色异常", e);
            return ResHelper.error("参数错误");
        }
    }

    public InfoUserVo infoUserById(String userId) {
        SysUser sysUser = sysUserDao.query().is("_id", userId).notReturnFields("salt", "password", "token", "tokenExpireTime").result();
        InfoUserVo infoUserVo = new InfoUserVo();
        if (sysUser != null) {
            BeanUtils.copyProperties(sysUser, infoUserVo);
            /**
             * TODO 设置电站信息 看需求是否需要
             */
            infoUserVo.setEssStationList(essStationDao.query().in("_id", sysUser.getStationList()).results());
        }
        return infoUserVo;
    }

    public ResHelper<BuguPageQuery.Page<InfoUserVo>> list(Map<String, Object> params) {
        BuguPageQuery<SysUser> query = sysUserDao.pageQuery();
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
        List<InfoUserVo> infoUserVos = getInfoUserVoList(userList);
        BuguPageQuery.Page<InfoUserVo> infoUserVoPage = new BuguPageQuery.Page<>(userResults.getPage(), userResults.getPageSize(), userResults.getTotalRecord(), infoUserVos);
        return ResHelper.success("", infoUserVoPage);
    }

    private List<InfoUserVo> getInfoUserVoList(List<SysUser> userList) {
        if (!CollectionUtils.isEmpty(userList) && !userList.isEmpty()) {
            return userList.stream().map(user -> {
                InfoUserVo infoUserVo = new InfoUserVo();
                List<RoleEntityOfUserBo> roleEntityOfUserBoList = new ArrayList<>();
                if (user.getId().equals("1")) {
                    RoleEntityOfUserBo roleEntity = new RoleEntityOfUserBo();
                    roleEntity.setRoleName("超级管理员");
                    roleEntityOfUserBoList.add(roleEntity);
                    infoUserVo.setRoleEntityLsit(roleEntityOfUserBoList);
                } else {
                    if (!CollectionUtils.isEmpty(user.getRoleIds()) && !user.getRoleIds().isEmpty()) {
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
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    infoUserVo.setEssStationList(essStationDao.query().in("_id", user.getStationList()).results());
                }
                return infoUserVo;
            }).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public ResHelper<Void> updateById(UpdateUserBo user) {
        try {
            if (sysUserDao.query().is("_id", String.valueOf(user.getId())).exists()) {
                if (user.getId() == 1) {
                    return ResHelper.error("管理员账号无法修改");
                }
                BuguUpdater<SysUser> update = sysUserDao.update();
                if (user.getStatus() != null) {
                    update.set("status", user.getStatus());
                }
                if (!StringUtils.isEmpty(user.getMobile())) {
                    if (!PatternUtil.isMobile(user.getMobile())) {
                        return ResHelper.error("手机号码格式错误");
                    }
                    if (sysUserDao.query().is("mobile", user.getMobile()).notEquals("_id", String.valueOf(user.getId())).exists()) {
                        return ResHelper.error("该手机号已存在");
                    }
                    update.set("mobile", user.getMobile());
                } else {
                    return ResHelper.error("参数错误");
                }

                if (!StringUtils.isEmpty(user.getEmail())) {
                    if (sysUserDao.query().is("email", user.getEmail()).notEquals("_id", String.valueOf(user.getId())).exists()) {
                        return ResHelper.error("该邮箱已存在");
                    }
                    update.set("email", user.getEmail());
                } else {
                    return ResHelper.error("参数错误");
                }
                if (!StringUtils.isEmpty(user.getUserExpireTimeStr())) {
                    if (!Const.DATE_PATTERN.matcher(user.getUserExpireTimeStr()).matches()) {
                        return ResHelper.error("账号过期时间:日期格式错误");
                    }
                    Date userExpireTime = DateUtils.getEndTime(DateUtils.dateToStrWithHHmm(user.getUserExpireTimeStr()));
                    if (!new Date().before(userExpireTime)) {
                        return ResHelper.error("账号过期时间:日期小于当前时间");
                    }
                    update.set("userExpireTime", userExpireTime);
                } else {
                    update.set("userExpireTime", null);
                }
                if (!StringUtils.isEmpty(user.getUserName())) {
                    if (sysUserDao.query().is("userName", user.getUserName()).notEquals("_id", String.valueOf(user.getId())).exists()) {
                        return ResHelper.error("该用户名已存在");
                    } else {
                        update.set("userName", user.getUserName());
                    }
                }
                if (!CollectionUtils.isEmpty(user.getRoleIds()) && !user.getRoleIds().isEmpty()) {
                    List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", user.getRoleIds()).results();
                    if (!CollectionUtils.isEmpty(roleEntities) && !roleEntities.isEmpty()) {
                        update.set("roleIds", user.getRoleIds());
                    } else {
                        return ResHelper.pamIll();
                    }
                }
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    for (String stationId : user.getStationList()) {
                        if (!essStationDao.exists(stationId)) {
                            return ResHelper.pamIll();
                        }
                    }
                    update.set("stationList", user.getStationList());
                } else {
                    update.set("stationList", new ArrayList<>());
                }
                if (!StringUtils.isEmpty(user.getRemarks())) {
                    update.set("remarks", user.getRemarks());
                }
                if (user.getUserType() == null) {
                    return ResHelper.pamIll();
                }
                if (user.getUserType() == 0 || user.getUserType() == 1) {
                    update.set("perms", "sys:device");
                } else if (user.getUserType() == 2) {
                    update.set("perms", null);
                } else {
                    return ResHelper.pamIll();
                }
                update.set("userType", user.getUserType());
                update.execute(sysUserDao.query().is("_id", String.valueOf(user.getId())));
                return ResHelper.success("修改用户成功");
            } else {
                return ResHelper.error("该用户不存在");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败:{}", e.getMessage());
        }
        return ResHelper.error("更新用户信息失败");

    }


    public ResHelper<LoginVo> loginByPassword(LoginFormBo loginFormBo) {
        SysUser sysUser = sysUserDao.query().is("userName", loginFormBo.getUserName()).result();
        if (sysUser == null || !sysUser.getPassword().equals(new Sha256Hash(loginFormBo.getPassword(), sysUser.getSalt()).toHex())) {
            return ResHelper.error("账号或密码错误");
        }
        if (sysUser.getStatus() != 1) {
            return ResHelper.error("账号已被禁用,请联系管理员");
        }
        if (sysUser.getUserExpireTime() != null && !sysUser.getUserExpireTime().after(new Date())) {
            return ResHelper.error("账号过期,请联系管理员");
        }
        //删除验证码 在生成验证码时候执行
//        captchaDao.remove(captchaDao.query().is("uuid", loginFormBo.getUuid()));

        return ResHelper.success("登录成功", createToken(sysUser));
    }

    public LoginVo createToken(SysUser sysUser) {
        LoginVo loginVo = new LoginVo();
        //生成一个token
        String token = TokenGenerator.generateValue();

        //过期时间
        Date expireTime = new Date(System.currentTimeMillis() + propertiesConfig.getTokenExpireTime());
        /**
         * 生成一个refreshToken
         */
        String refreshToken = TokenGenerator.generateValue();
        Date refreshTokenExpireTime = new Date(System.currentTimeMillis() + 2 * propertiesConfig.getTokenExpireTime());
        sysUserDao.update().set("token", token)
                .set("tokenExpireTime", expireTime)
                .set("createTokenTime", new Date(System.currentTimeMillis()))
                .set("refreshToken", refreshToken)
                .set("refreshTokenExpireTime", refreshTokenExpireTime)
                .execute(sysUserDao.query().is("_id", sysUser.getId()));
        loginVo.setToken(token);
        loginVo.setTokenExpireTime(expireTime);
        loginVo.setRefreshToken(refreshToken);
        loginVo.setRefreshTokenExpireTime(refreshTokenExpireTime);
        loginVo.setId(sysUser.getId());
        loginVo.setUserType(sysUser.getUserType());
        return loginVo;
    }

    public ResHelper<Void> deleteUser(List<String> userIds) {
        if (!CollectionUtils.isEmpty(userIds) && !userIds.isEmpty()) {
            sysUserDao.remove(userIds);
        }
        return ResHelper.success("删除成功");
    }

    public String queryAmindId() {
        SysUser sysUser = sysUserDao.query().is("_id", "1").result();
        return sysUser.getId();
    }

    public ResHelper<Void> password(PasswordForm passwordForm, SysUser sysUser) {
        /**
         * 验证密码格式
         */
        if (!StringUtils.isEmpty(passwordForm.getNewPassword())) {
            if (!passwordForm.getNewPassword().matches(PASSWORD_PATTERN)) {
                return ResHelper.error("请输入英文与数字组合的6至8位数密码!");
            }
        }
        //sha256加密
        String password = new Sha256Hash(passwordForm.getPassword(), sysUser.getSalt()).toHex();
        passwordForm.setPassword(password);
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
            passwordForm.setNewPassword(new Sha256Hash(passwordForm.getNewPassword(), salt).toHex());
            sysUserDao.update().set("password", passwordForm.getNewPassword())
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
        SysUser sysUser = sysUserDao.query().is("refreshToken", refreshToken).result();
        if (sysUser != null) {
            if (sysUser.getRefreshToken().equals(refreshToken) && !sysUser.getRefreshTokenExpireTime().before(new Date())) {
                return ResHelper.success("刷新token成功", this.createToken(sysUser));
            }
        }
        return ResHelper.error("刷新token过期,请重新登录");
    }

    public ResHelper<Void> logout(SysUser sysUser) {
        this.createToken(sysUser);
        return ResHelper.success("登出成功");
    }

    public ResHelper<Void> resetPwd(List<String> users) {
        if (!CollectionUtils.isEmpty(users) && !users.isEmpty()) {
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

    public ResHelper<BuguPageQuery.Page<InfoUserVo>> list(Integer page, Integer pageSize, String userName) {
        BuguPageQuery<SysUser> query = sysUserDao.pageQuery();
        if (!StringUtils.isEmpty(userName)) {
            query.regexCaseInsensitive("userName", userName);
        }
        query.notReturnFields("salt", "password", "token", "tokenExpireTime");
        query.pageSize(pageSize).pageNumber(page).sortDesc("_id");
        BuguPageQuery.Page<SysUser> userResults = query.resultsWithPage();
        List<SysUser> userList = userResults.getDatas();
        List<InfoUserVo> infoUserVos = getInfoUserVoList(userList);
        BuguPageQuery.Page<InfoUserVo> infoUserVoPage = new BuguPageQuery.Page<>(userResults.getPage(), userResults.getPageSize(), userResults.getTotalRecord(), infoUserVos);
        return ResHelper.success("", infoUserVoPage);
    }
}