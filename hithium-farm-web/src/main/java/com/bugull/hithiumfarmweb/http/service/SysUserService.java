package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.bo.PasswordForm;
import com.bugull.hithiumfarmweb.http.bo.RoleEntityOfUserBo;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.dao.EssStationDao;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.SysUserDao;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.oauth2.TokenGenerator;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
            Query existsUserNameQuery = new Query();
            existsUserNameQuery.addCriteria(Criteria.where("userName").is(sysUser.getUserName()));

            Query existMobileQuery = new Query();
            existMobileQuery.addCriteria(Criteria.where("mobile").is(sysUser.getMobile()));

            Query existEmailQuery = new Query();
            existEmailQuery.addCriteria(Criteria.where("email").is(sysUser.getEmail()));
            if (sysUserDao.exists(existsUserNameQuery) || sysUserDao.exists(existMobileQuery) || sysUserDao.exists(existEmailQuery)) {
                return ResHelper.error("用户名/手机号/邮箱已存在");
            }
            if (!CollectionUtils.isEmpty(sysUser.getStationList()) && !sysUser.getStationList().isEmpty()) {
                for (String stationId : sysUser.getStationList()) {
                    Query existEssStation = new Query();
                    existEssStation.addCriteria(Criteria.where("_id").is(stationId));
                    if (!essStationDao.exists(existEssStation)) {
                        return ResHelper.pamIll();
                    }
                }
            }
            if (!CollectionUtils.isEmpty(sysUser.getRoleIds()) && !sysUser.getRoleIds().isEmpty()) {
                /**
                 * 验证roleIds的参数
                 */
                Query roleBatch = new Query();
                roleBatch.addCriteria(Criteria.where("_id").in(sysUser.getRoleIds()));
                List<RoleEntity> roleEntities = roleEntityDao.findBatch(roleBatch);
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
            sysUserDao.saveUser(sysUser);
            return ResHelper.success("添加用户成功");
        } catch (Exception e) {
            log.error("添加角色异常", e);
            return ResHelper.error("参数错误");
        }
    }

    public InfoUserVo infoUserById(Long userId) {
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("_id").is(userId)).fields().exclude("salt").exclude("password").exclude("token").exclude("tokenExpireTime");
        SysUser sysUser = sysUserDao.findUser(findQuery);
        InfoUserVo infoUserVo = new InfoUserVo();
        if (sysUser != null) {
            BeanUtils.copyProperties(sysUser, infoUserVo);
            /**
             * TODO 设置电站信息 看需求是否需要
             */
            Query essStationQuery = new Query();
            essStationQuery.addCriteria(Criteria.where("_id").in(sysUser.getStationList()));
            List<EssStation> essStationList = essStationDao.findEssStationBatch(essStationQuery);
            infoUserVo.setEssStationList(essStationList);
            return infoUserVo;
        }
        return null;

    }


    private List<InfoUserVo> getInfoUserVoList(List<SysUser> userList) {
        if (!CollectionUtils.isEmpty(userList) && !userList.isEmpty()) {
            return userList.stream().map(user -> {
                InfoUserVo infoUserVo = new InfoUserVo();
                List<RoleEntityOfUserBo> roleEntityOfUserBoList = new ArrayList<>();
                if (user.getId()==1L) {
                    RoleEntityOfUserBo roleEntity = new RoleEntityOfUserBo();
                    roleEntity.setRoleName("超级管理员");
                    roleEntityOfUserBoList.add(roleEntity);
                    infoUserVo.setRoleEntityList(roleEntityOfUserBoList);
                } else {
                    if (!CollectionUtils.isEmpty(user.getRoleIds()) && !user.getRoleIds().isEmpty()) {
                        Query roleQuery = new Query();
                        roleQuery.addCriteria(Criteria.where("_id").in(user.getRoleIds()));
                        List<RoleEntity> roleEntities = roleEntityDao.findBatch(roleQuery);
                        BeanUtils.copyProperties(user, infoUserVo);
                        for (RoleEntity roleEntity : roleEntities) {
                            RoleEntityOfUserBo roleEntityOfUserBo = new RoleEntityOfUserBo();
                            BeanUtils.copyProperties(roleEntity, roleEntityOfUserBo);
                            roleEntityOfUserBoList.add(roleEntityOfUserBo);
                        }
                        infoUserVo.setRoleEntityList(roleEntityOfUserBoList);
                    }
                }
                infoUserVo.setId(user.getId());
                BeanUtils.copyProperties(user, infoUserVo);
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    Query essStationQuery = new Query();
                    essStationQuery.addCriteria(Criteria.where("_id").in(user.getStationList()));
                    List<EssStation> essStationBatch = essStationDao.findEssStationBatch(essStationQuery);
                    infoUserVo.setEssStationList(essStationBatch);
                }
                return infoUserVo;
            }).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public ResHelper<Void> updateById(UpdateUserBo user) {
        try {
            Query existsQuery = new Query();
            existsQuery.addCriteria(Criteria.where("_id").is(user.getId()));
            if (sysUserDao.exists(existsQuery)) {
                if (user.getId() == 1) {
                    return ResHelper.error("管理员账号无法修改");
                }
                Update sysUserUpdate = new Update();
                if (user.getStatus() != null) {
                    sysUserUpdate.set("status", user.getStatus());
                }
                if (!StringUtils.isEmpty(user.getMobile())) {
                    if (!PatternUtil.isMobile(user.getMobile())) {
                        return ResHelper.error("手机号码格式错误");
                    }
                    Query existsSysUserQuery = new Query();
                    existsSysUserQuery.addCriteria(Criteria.where("mobile").is(user.getMobile()))
                            .addCriteria(Criteria.where("_id").ne(String.valueOf(user.getId())));
                    if (sysUserDao.exists(existsSysUserQuery)) {
                        return ResHelper.error("该手机号已存在");
                    }
                    sysUserUpdate.set("mobile", user.getMobile());
                } else {
                    return ResHelper.error("参数错误");
                }

                if (!StringUtils.isEmpty(user.getEmail())) {
                    Query existsSysUserQuery = new Query();
                    existsSysUserQuery.addCriteria(Criteria.where("email").is(user.getEmail()))
                            .addCriteria(Criteria.where("id").ne(String.valueOf(user.getId())));
                    if (sysUserDao.exists(existsSysUserQuery)) {
                        return ResHelper.error("该邮箱已存在");
                    }
                    sysUserUpdate.set("email", user.getEmail());
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
                    sysUserUpdate.set("userExpireTime", userExpireTime);
                } else {
                    sysUserUpdate.set("userExpireTime", null);
                }
                if (!StringUtils.isEmpty(user.getUserName())) {
                    Query existsSysUserQuery = new Query();
                    existsSysUserQuery.addCriteria(Criteria.where("userName").is(user.getUserName()))
                            .addCriteria(Criteria.where("_id").ne(user.getId()));
                    if (sysUserDao.exists(existsSysUserQuery)) {
                        return ResHelper.error("该用户名已存在");
                    } else {
                        sysUserUpdate.set("userName", user.getUserName());
                    }
                }
                if (!CollectionUtils.isEmpty(user.getRoleIds()) && !user.getRoleIds().isEmpty()) {
                    Query batchRoleQuery = new Query();
                    batchRoleQuery.addCriteria(Criteria.where("_id").in(user.getRoleIds()));
                    List<RoleEntity> roleEntities = roleEntityDao.findBatch(batchRoleQuery);
                    if (!CollectionUtils.isEmpty(roleEntities) && !roleEntities.isEmpty()) {
                        sysUserUpdate.set("roleIds", user.getRoleIds());
                    } else {
                        return ResHelper.pamIll();
                    }
                }
                if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                    for (String stationId : user.getStationList()) {
                        Query existsQueryId = new Query();
                        existsQueryId.addCriteria(Criteria.where("_id").is(stationId));
                        if (!essStationDao.exists(existsQueryId)) {
                            return ResHelper.pamIll();
                        }
                    }
                    sysUserUpdate.set("stationList", user.getStationList());
                } else {
                    sysUserUpdate.set("stationList", new ArrayList<>());
                }
                if (!StringUtils.isEmpty(user.getRemarks())) {
                    sysUserUpdate.set("remarks", user.getRemarks());
                }
                if (user.getUserType() == null) {
                    return ResHelper.pamIll();
                }
                if (user.getUserType() == 0 || user.getUserType() == 1) {
                    sysUserUpdate.set("perms", "sys:device");
                } else if (user.getUserType() == 2) {
                    sysUserUpdate.set("perms", null);
                } else {
                    return ResHelper.pamIll();
                }
                sysUserUpdate.set("userType", user.getUserType());

                Query updateQuery = new Query();
                updateQuery.addCriteria(Criteria.where("_id").is(user.getId()));
                sysUserDao.updateByQuery(updateQuery,sysUserUpdate);
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
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("userName").is( loginFormBo.getUserName()));
        SysUser sysUser = sysUserDao.findUser(findQuery);
        if (sysUser == null || !sysUser.getPassword().equals(new Sha256Hash(loginFormBo.getPassword(), sysUser.getSalt()).toHex())) {
            return ResHelper.error("账号或密码错误");
        }
        if (sysUser.getStatus() != 1) {
            return ResHelper.error("账号已被禁用,请联系管理员");
        }
        if (sysUser.getUserExpireTime() != null && !sysUser.getUserExpireTime().after(new Date())) {
            return ResHelper.error("账号过期,请联系管理员");
        }
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
        Update update = new Update();
        update.set("token", token)
                .set("tokenExpireTime", expireTime)
                .set("createTokenTime", new Date(System.currentTimeMillis()))
                .set("refreshToken", refreshToken)
                .set("refreshTokenExpireTime", refreshTokenExpireTime);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(sysUser.getId()));
        sysUserDao.updateByQuery(query,update);
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
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is( userIds.stream().map(Long::valueOf).collect(Collectors.toList())));
            sysUserDao.removeByQuery(query);
        }
        return ResHelper.success("删除成功");
    }

    public Long queryAdminId() {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(1));
        SysUser sysUser = sysUserDao.findUser(query);
        if(sysUser != null){
            return sysUser.getId();
        }
        return null;

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
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("userName").is(sysUser.getUserName()));
        SysUser userName = sysUserDao.findUser(findQuery);
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
            Update update = new Update();
            update.set("password", passwordForm.getNewPassword())
                    .set("salt", salt)
                    .set("token", TokenGenerator.generateValue())
                    .set("tokenExpireTime", expireTime)
                    .set("refreshToken", TokenGenerator.generateValue())
                    .set("refreshTokenExpireTime", refreshTokenExpireTime);
            Query updateQuery = new Query();
            updateQuery.addCriteria(Criteria.where("_id").is(userName.getId()));
            sysUserDao.updateByQuery(updateQuery,update);
            return ResHelper.success("修改密码成功,请重新登录");
        } else {
            return ResHelper.error("原密码不正确");
        }
    }

    public ResHelper<LoginVo> refreshToken(String refreshToken) {
        if (StringUtils.isEmpty(refreshToken)) {
            return ResHelper.pamIll();
        }
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("refreshToken").is(refreshToken));
        SysUser sysUser =  sysUserDao.findUser(findQuery);
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
                Update update = new Update();
                update.set("salt", salt)
                        .set("password", resetPwd)
                        .set("token", TokenGenerator.generateValue())
                        .set("tokenExpireTime", expireTime)
                        .set("refreshToken", TokenGenerator.generateValue())
                        .set("refreshTokenExpireTime", refreshTokenExpireTime);
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(Long.valueOf(userId)));
                sysUserDao.updateByQuery(query,update);
            });
            return ResHelper.success("重置密码成功");
        }
        return ResHelper.error("");
    }

    public ResHelper<Page<InfoUserVo>> list(Integer page, Integer pageSize, String userName) {
        Query batchUserQuery = new Query();
        if (!StringUtils.isEmpty(userName)) {
            batchUserQuery.addCriteria(Criteria.where("userName").regex(userName));
        }
        batchUserQuery.fields().exclude("salt").exclude("password").exclude("token").exclude("tokenExpireTime");
        long totalRecord = sysUserDao.countNumberUser(batchUserQuery);
        batchUserQuery.skip(((long) page - 1) * pageSize).limit(pageSize).with(Sort.by(Sort.Order.desc("_id")));
        List<SysUser> userList = sysUserDao.findBatchUser(batchUserQuery);
        List<InfoUserVo> infoUserVos = getInfoUserVoList(userList);
        return ResHelper.success("", new Page<>(page, pageSize, totalRecord, infoUserVos));
    }
}