package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BmsCellTempDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class BmsCellTempDataDicDao extends BuguDao<BmsCellTempDataDic> {
    public BmsCellTempDataDicDao() {
        super(BmsCellTempDataDic.class);
    }
}
