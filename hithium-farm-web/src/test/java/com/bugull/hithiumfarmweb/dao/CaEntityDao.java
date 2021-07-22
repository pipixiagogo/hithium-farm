package com.bugull.hithiumfarmweb.dao;

import com.bugull.hithiumfarmweb.entity.CaEntity;
import com.bugull.mongo.BuguDao;

public class CaEntityDao extends BuguDao<CaEntity> {
    public CaEntityDao() {
        super(CaEntity.class);
    }
}
