package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.TemperatureMeterDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class TemperatureMeterDataDicDao extends BuguDao<TemperatureMeterDataDic> {
    public TemperatureMeterDataDicDao() {
        super(TemperatureMeterDataDic.class);
    }
}
