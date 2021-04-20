package com.bugull.hithiumfarmweb.http.dao;


import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public class SysUserDao extends BuguPageDao<SysUser> {
    public SysUserDao() {
        super(SysUser.class);
    }
}
