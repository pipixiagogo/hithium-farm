package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.UpsPowerDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class UpsPowerDataDicDao extends BuguDao<UpsPowerDataDic> {
    public UpsPowerDataDicDao() {
        super(UpsPowerDataDic.class);
    }
}
