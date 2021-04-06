package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BmsCellVoltDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BmsCellVoltDataDicDao extends BuguDao<BmsCellVoltDataDic> {
    public BmsCellVoltDataDicDao() {
        super(BmsCellVoltDataDic.class);
    }
}
