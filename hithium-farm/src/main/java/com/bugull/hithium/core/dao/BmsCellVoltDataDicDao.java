package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BmsCellVoltDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class BmsCellVoltDataDicDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveBmsCellVoltData(BmsCellVoltDataDic bmsCellVoltDataDic) {
        mongoTemplate.insert(bmsCellVoltDataDic, ClassUtil.getClassLowerName(BmsCellVoltDataDic.class));
    }
}
