package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.FireControlDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class FireControlDataDicDao extends BuguDao<FireControlDataDic> {
    public FireControlDataDicDao() {
        super(FireControlDataDic.class);
    }
}
