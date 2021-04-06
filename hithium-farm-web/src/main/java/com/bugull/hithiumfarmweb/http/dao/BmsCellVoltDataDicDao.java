package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.BmsCellVoltDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class BmsCellVoltDataDicDao extends BuguPageDao<BmsCellVoltDataDic> {
    public BmsCellVoltDataDicDao() {
        super(BmsCellVoltDataDic.class);
    }
}
