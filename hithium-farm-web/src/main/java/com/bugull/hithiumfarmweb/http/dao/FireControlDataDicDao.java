package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.FireControlDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class FireControlDataDicDao extends BuguPageDao<FireControlDataDic> {
    public FireControlDataDicDao() {
        super(FireControlDataDic.class);
    }
}
