package com.bugull.hithiumfarmweb.dao;

import com.bugull.hithiumfarmweb.entity.BcuEntity;
import com.bugull.mongo.BuguDao;

public class BcuEntityDao extends BuguDao<BcuEntity> {
    public BcuEntityDao() {
        super(BcuEntity.class);
    }
}
