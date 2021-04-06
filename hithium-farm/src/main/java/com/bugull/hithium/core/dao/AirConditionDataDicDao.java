package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.AirConditionDataDic;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class AirConditionDataDicDao extends BuguDao<AirConditionDataDic> {
    public AirConditionDataDicDao() {
        super(AirConditionDataDic.class);
    }
}
