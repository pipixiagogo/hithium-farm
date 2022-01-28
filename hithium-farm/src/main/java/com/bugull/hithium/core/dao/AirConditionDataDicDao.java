package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.AirConditionDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AirConditionDataDicDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveAirConditionData(AirConditionDataDic airConditionDataDic) {
        mongoTemplate.insert(airConditionDataDic, ClassUtil.getClassLowerName(AirConditionDataDic.class));
    }
}
