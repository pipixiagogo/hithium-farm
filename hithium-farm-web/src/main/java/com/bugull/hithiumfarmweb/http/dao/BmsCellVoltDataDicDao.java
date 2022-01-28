package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BmsCellVoltDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BmsCellVoltDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(BmsCellVoltDataDic.class),BasicDBObject.class).getMappedResults();
    }

    public BmsCellVoltDataDic findBmsCellVol(Query query) {
        return mongoTemplate.findOne(query,BmsCellVoltDataDic.class,ClassUtil.getClassLowerName(BmsCellVoltDataDic.class));
    }

    public List<BmsCellVoltDataDic> findBatchQuery(Query query) {
        return mongoTemplate.find(query,BmsCellVoltDataDic.class,ClassUtil.getClassLowerName(BmsCellVoltDataDic.class));
    }

    public long count(Query query) {
        return mongoTemplate.count(query,BmsCellVoltDataDic.class,ClassUtil.getClassLowerName(BmsCellVoltDataDic.class));
    }
}
