package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.TemperatureMeterDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class TemperatureMeterDataDicDao extends BuguPageDao<TemperatureMeterDataDic> {
    public TemperatureMeterDataDicDao() {
        super(TemperatureMeterDataDic.class);
    }
}
