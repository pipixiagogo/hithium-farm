package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.AmmeterDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Locale;

@Repository
public class AmmeterDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;

    public void saveAmmeterDataDicDao(AmmeterDataDic ammeterDataDic){
        mongoTemplate.insert(ammeterDataDic, ClassUtil.getClassLowerName(AmmeterDataDic.class));
    }

    public AmmeterDataDic fromAggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation,ClassUtil.getClassLowerName(AmmeterDataDic.class),AmmeterDataDic.class).getMappedResults().get(0);
    }
}
