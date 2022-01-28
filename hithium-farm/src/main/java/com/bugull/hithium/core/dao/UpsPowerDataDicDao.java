package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.UpsPowerDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class UpsPowerDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public void saveUpsPowerData(UpsPowerDataDic upsPowerDataDic) {
        mongoTemplate.insert(upsPowerDataDic, ClassUtil.getClassLowerName(UpsPowerDataDic.class));
    }
}
