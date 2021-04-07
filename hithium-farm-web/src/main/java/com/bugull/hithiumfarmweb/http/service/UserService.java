package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.bo.PasswordForm;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.dao.RoleEntityDao;
import com.bugull.hithiumfarmweb.http.dao.UserDao;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.http.entity.User;
import com.bugull.hithiumfarmweb.http.oauth2.TokenGenerator;
import com.bugull.hithiumfarmweb.http.vo.InfoUserVo;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.PropertyUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguUpdater;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    //12小时后过期
    private final static int EXPIRE = 3600 * 12;
    //    private final static int EXPIRE = 60 * 5;
    @Resource
    private UserDao userDao;
    @Resource
    private RoleEntityDao roleEntityDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    public ResHelper<Void> saveUser(User user) {
        if (!ResHelper.validatePhone(user.getMobile())) {
            return ResHelper.error("手机号格式错误");
        }
        /**
         * 邮箱+用户名相同  或者 电话 +用户名相同 即为同一用户
         */
        if (userDao.query().is("userName", user.getUserName()).exists()) {
            return ResHelper.error("用户已存在");
        }
        if (userDao.query().is("email", user.getEmail()).exists() ||
                userDao.query().is("mobile", user.getMobile()).exists()) {
            return ResHelper.error("邮箱或手机号已被注册");
        }
        try {
            if (user.getRoleIds() != null && user.getRoleIds().size() > 0) {
                /**
                 * 验证roleIds的参数
                 */
                List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", user.getRoleIds()).results();
                if (roleEntities != null && roleEntities.size() > 0) {

                } else {
                    return ResHelper.pamIll();
                }
            }
            user.setCreateTime(new Date());
            //sha256加密
            String salt = RandomStringUtils.randomAlphanumeric(20);
            user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
            user.setSalt(salt);
            userDao.insert(user);
            return ResHelper.success("添加用户成功");
        } catch (Exception e) {
            log.error("查询角色异常", e);
            return ResHelper.error("参数错误");
        }
    }

    public InfoUserVo infoUserById(String userId) {
        User user = userDao.query().is("_id", userId).notReturnFields("salt", "password", "token", "tokenExpireTime").result();
        InfoUserVo infoUserVo = new InfoUserVo();
        if (user != null) {
            BeanUtils.copyProperties(user, infoUserVo);
        }
        return infoUserVo;
    }

    public ResHelper<BuguPageQuery.Page<InfoUserVo>> list(Map<String, Object> params) {
        BuguPageQuery<User> query = (BuguPageQuery<User>) userDao.pageQuery();
        String userName = (String) params.get("userName");
        if (!StringUtils.isBlank(userName)) {
            query.is("userName", userName);
        }
        query.notReturnFields("salt", "password", "token", "tokenExpireTime");
        if (!PagetLimitUtil.pageLimit(query, params)) {
            return ResHelper.pamIll();
        }
        query.sortDesc("_id");
        BuguPageQuery.Page<User> userResults = query.resultsWithPage();
        List<User> userList = userResults.getDatas();
        List<InfoUserVo> infoUserVos;
        if (userList != null && userList.size() > 0) {
            infoUserVos = userList.stream().map(user -> {
                InfoUserVo infoUserVo = new InfoUserVo();
                BeanUtils.copyProperties(user, infoUserVo);
                return infoUserVo;
            }).collect(Collectors.toList());
        } else {
            infoUserVos = new ArrayList<>();
        }
        BuguPageQuery.Page<InfoUserVo> infoUserVoPage = new BuguPageQuery.Page<>(userResults.getPage(), userResults.getPageSize(), userResults.getTotalPage(), infoUserVos);
        return ResHelper.success("", infoUserVoPage);

    }

    public ResHelper<Void> updateById(UpdateUserBo user) {
        /**
         * 验证手机号格式
         */
        if (!ResHelper.validatePhone(user.getMobile())) {
            return ResHelper.error("手机号格式错误");
        }
        BuguUpdater<User> update = userDao.update();
        if (userDao.query().is("email", user.getEmail()).exists() || userDao.query().is("mobile", user.getMobile()).exists()) {
            return ResHelper.error("邮箱/手机号已存在");
        }
        if (!StringUtils.isBlank(user.getMobile())) {
            update.set("mobile", user.getMobile());
        }
        if (!StringUtils.isBlank(user.getEmail())) {
            update.set("email", user.getEmail());
        }
        if (user.getRoleIds() != null && user.getRoleIds().size() > 0) {
            List<RoleEntity> roleEntities = roleEntityDao.query().in("_id", user.getRoleIds()).results();
            if (roleEntities != null && roleEntities.size() > 0) {
                update.set("roleIds", user.getRoleIds());
            } else {
                return ResHelper.pamIll();
            }
        }
        update.execute(userDao.query().is("_id", String.valueOf(user.getId())));
        return ResHelper.success("修改用户成功");
    }


    public ResHelper<LoginVo> loginByPassword(LoginFormBo loginFormBo) {
        User user = userDao.query().is("userName", loginFormBo.getUserName()).result();
        if (user == null || !user.getPassword().equals(new Sha256Hash(loginFormBo.getPassword(), user.getSalt()).toHex())) {
            return ResHelper.error("账号或密码错误");
        }
        if (user.getStatus() != 1) {
            return ResHelper.error("账号被锁定");
        }
        return ResHelper.success("登录成功", createToken(user.getId()));
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
        userDao.update().set("token", token)
                .set("tokenExpireTime", expireTime)
                .set("createTokenTime", now)
                .set("refreshToken", refreshToken)
                .set("refreshTokenExpireTime", refreshTokenExpireTime)
                .execute(userDao.query().is("_id", id));
        loginVo.setToken(token);
        loginVo.setTokenExpireTime(expireTime);
        loginVo.setRefreshToken(refreshToken);
        loginVo.setRefreshTokenExpireTime(refreshTokenExpireTime);
        return loginVo;
    }

    public ResHelper<Void> deleteUser(List<String> userIds) {
        if (userIds != null && userIds.size() > 0) {
            userDao.remove(userIds);
        }
        return ResHelper.success("删除成功");
    }

    public String queryAmindId() {
        User user = userDao.query().is("_id", "1").is("userName", "admin").result();
        return user.getId();
    }

    public ResHelper<Void> password(PasswordForm passwordForm, User user) {
        //sha256加密
        String password = new Sha256Hash(passwordForm.getPassword(), user.getSalt()).toHex();
        //sha256加密
        User userName = userDao.query().is("userName", user.getUserName()).result();
        if (userName != null && user.getPassword().equals(password)) {
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + propertiesConfig.getTokenExpireTime());
            Date refreshTokenExpireTime = new Date(now.getTime() + 2 * propertiesConfig.getTokenExpireTime());
            /**
             * 修改密码加修改盐
             */
            String salt = RandomStringUtils.randomAlphanumeric(20);
            userDao.update().set("password",  new Sha256Hash(passwordForm.getNewPassword(), salt).toHex())
                    .set("salt",salt)
                    .set("token",TokenGenerator.generateValue())
                    .set("tokenExpireTime",expireTime)
                    .set("refreshToken",TokenGenerator.generateValue())
                    .set("refreshTokenExpireTime",refreshTokenExpireTime)
                    .execute(user);
            return ResHelper.success("修改密码成功");
        } else {
            return ResHelper.error("原密码不正确");
        }
    }

    public ResHelper<LoginVo> refreshToken(String refreshToken) {
        User result = userDao.query().is("refreshToken", refreshToken).result();
        if (result != null) {
            if (result.getRefreshToken().equals(refreshToken) && !result.getRefreshTokenExpireTime().before(new Date())) {
                return ResHelper.success("刷新token成功", this.createToken(result.getId()));
            }
        }
        return ResHelper.error("刷新token过期,请重新登录");
    }

    public ResHelper<Void> logout(User user) {
//        userDao.update().set("token",null)
//                .set("tokenExpireTime",null)
//                .set("refreshToken",null)
//                .set("refreshTokenExpireTime",null)
//                .execute(user);
        this.createToken(user.getId());
        return ResHelper.success("登出成功");
    }

    public ResHelper<Void> resetPwd(List<String> users) {
        if (users != null && users.size() > 0) {
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + propertiesConfig.getTokenExpireTime());
            Date refreshTokenExpireTime = new Date(now.getTime() + 2 * propertiesConfig.getTokenExpireTime());
            users.forEach(userId->{
                String salt = RandomStringUtils.randomAlphanumeric(20);
                String resetPwd = new Sha256Hash("123456", salt).toHex();
                userDao.update().set("salt",salt)
                        .set("password",resetPwd)
                        .set("token",TokenGenerator.generateValue())
                        .set("tokenExpireTime",expireTime)
                        .set("refreshToken",TokenGenerator.generateValue())
                        .set("refreshTokenExpireTime",refreshTokenExpireTime)
                        .execute(userId);
            });
            return ResHelper.success("重置密码成功");
//            userDao.update().set("")
        }
        return ResHelper.error("");
    }
}
