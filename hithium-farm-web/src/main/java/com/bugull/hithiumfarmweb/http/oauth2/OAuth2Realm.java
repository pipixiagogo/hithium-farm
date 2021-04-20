/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.bugull.hithiumfarmweb.http.oauth2;

import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.service.ShiroService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 认证
 * 实现AuthorizingRealm接口用户
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Resource
    @Lazy
    private ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)  登录成功调用该方法授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUser userPrincipal = (SysUser) principals.getPrimaryPrincipal();
        //授权过程 用户权限列表
        Set<String> permsSet = shiroService.getPermissions(userPrincipal);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     * 调用subject.login(token);  则会调用 doGetAuthenticationInfo 进行登录
     * <p>
     * 登录失败后会调用  onLoginFailure 进行失败处理
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //得到token
        String accessToken = (String) token.getPrincipal();
        // 根据accessToken，查询用户信息
        SysUser tokenEntity = shiroService.queryByToken(accessToken);
        //token失效
        if (tokenEntity == null ){
           throw new IncorrectCredentialsException("无效token,请重新登录");
        }
        if(tokenEntity.getTokenExpireTime().getTime() < System.currentTimeMillis()) {
            throw new IncorrectCredentialsException("token过期,请重新登录");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(tokenEntity, accessToken, getName());
        return info;
    }
}
