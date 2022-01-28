package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.TemperatureMeterDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class TemperatureMeterDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(TemperatureMeterDataDic.class), BasicDBObject.class).getMappedResults();
    }

    public TemperatureMeterDataDic findTemperatureMeterData(Query query) {
        return mongoTemplate.findOne(query,TemperatureMeterDataDic.class,ClassUtil.getClassLowerName(TemperatureMeterDataDic.class));
    }

    public List<TemperatureMeterDataDic> findBatchTemperature(Query query) {
        return mongoTemplate.find(query,TemperatureMeterDataDic.class,ClassUtil.getClassLowerName(TemperatureMeterDataDic.class));
    }

    public long count(Query temperatureQuery) {
        return mongoTemplate.count(temperatureQuery,TemperatureMeterDataDic.class,ClassUtil.getClassLowerName(TemperatureMeterDataDic.class));
    }
}
