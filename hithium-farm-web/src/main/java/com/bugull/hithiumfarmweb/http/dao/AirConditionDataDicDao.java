package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.http.entity.AirConditionDataDic;
import org.springframework.stereotype.Repository;

@Repository
public class AirConditionDataDicDao extends BuguPageDao<AirConditionDataDic> {
    public AirConditionDataDicDao() {
        super(AirConditionDataDic.class);
    }
}
