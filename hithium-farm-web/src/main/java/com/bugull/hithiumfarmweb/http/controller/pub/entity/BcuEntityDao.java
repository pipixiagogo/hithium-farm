package com.bugull.hithiumfarmweb.http.controller.pub.entity;

import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BcuEntityDao extends BuguDao<BcuEntity> {
    public BcuEntityDao() {
        super(BcuEntity.class);
    }
}
