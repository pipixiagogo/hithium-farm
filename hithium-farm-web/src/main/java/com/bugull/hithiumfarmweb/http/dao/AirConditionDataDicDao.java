package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.AirConditionDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AirConditionDataDicDao {

    @Resource
    private MongoTemplate mongoTemplate;
    public List<AirConditionDataDic> findBatchAirCondition(Query airConditionQuery) {
        return mongoTemplate.find(airConditionQuery,AirConditionDataDic.class, ClassUtil.getClassLowerName(AirConditionDataDic.class));
    }

    public long count(Query airConditionQuery) {
        return mongoTemplate.count(airConditionQuery,AirConditionDataDic.class,ClassUtil.getClassLowerName(AirConditionDataDic.class));
    }

    public Object findAirCondition(Query query) {
        return mongoTemplate.findOne(query,AirConditionDataDic.class,ClassUtil.getClassLowerName(AirConditionDataDic.class));
    }
}
