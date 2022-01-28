package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BcuDataDicBCU;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class BcuDataDicBCUDao {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveBcuData(BcuDataDicBCU bcuDataDicBCU) {
        mongoTemplate.insert(bcuDataDicBCU, ClassUtil.getClassLowerName(BcuDataDicBCU.class));
    }
}
