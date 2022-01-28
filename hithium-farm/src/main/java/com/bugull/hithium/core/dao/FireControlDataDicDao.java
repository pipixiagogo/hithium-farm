package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.FireControlDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FireControlDataDicDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveFireControlData(FireControlDataDic fireControlDataDic) {
        mongoTemplate.insert(fireControlDataDic, ClassUtil.getClassLowerName(FireControlDataDic.class));
    }
}
