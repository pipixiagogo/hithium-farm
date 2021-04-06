package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.AmmeterDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class AmmeterDataDicDao extends BuguPageDao<AmmeterDataDic> {
    public AmmeterDataDicDao() {
        super(AmmeterDataDic.class);
    }
}
