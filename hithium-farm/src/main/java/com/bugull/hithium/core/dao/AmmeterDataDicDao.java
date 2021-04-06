package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.AmmeterDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class AmmeterDataDicDao extends BuguDao<AmmeterDataDic> {
    public AmmeterDataDicDao() {
        super(AmmeterDataDic.class);
    }
}
