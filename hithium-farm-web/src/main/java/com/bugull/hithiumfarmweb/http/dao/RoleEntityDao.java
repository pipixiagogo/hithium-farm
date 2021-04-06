package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class RoleEntityDao extends BuguPageDao<RoleEntity> {
    public RoleEntityDao() {
        super(RoleEntity.class);
    }
}
