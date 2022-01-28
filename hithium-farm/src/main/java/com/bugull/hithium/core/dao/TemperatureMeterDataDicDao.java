package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.TemperatureMeterDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class TemperatureMeterDataDicDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveTemperatureMeterData(TemperatureMeterDataDic temperatureMeterDataDic) {
        mongoTemplate.insert(temperatureMeterDataDic, ClassUtil.getClassLowerName(TemperatureMeterDataDic.class));
    }
}
