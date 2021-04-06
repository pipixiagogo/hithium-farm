package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.UpsPowerDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class UpsPowerDataDicDao extends BuguPageDao<UpsPowerDataDic> {
    public UpsPowerDataDicDao() {
        super(UpsPowerDataDic.class);
    }
}
