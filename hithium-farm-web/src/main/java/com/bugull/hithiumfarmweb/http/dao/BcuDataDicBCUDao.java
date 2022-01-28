package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BcuDataDicBCU;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BcuDataDicBCUDao {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(BcuDataDicBCU.class), BasicDBObject.class).getMappedResults();
    }


    public BcuDataDicBCU findBcuData(Query findBatchQuery) {
        return mongoTemplate.findOne(findBatchQuery,BcuDataDicBCU.class,ClassUtil.getClassLowerName(BcuDataDicBCU.class));
    }

    public List<BcuDataDicBCU> findBatchBcuData(Query bcuDataQuery) {
        return mongoTemplate.find(bcuDataQuery,BcuDataDicBCU.class,ClassUtil.getClassLowerName(BcuDataDicBCU.class));
    }

    public long count(Query bcuDataQuery) {
        return mongoTemplate.count(bcuDataQuery,BcuDataDicBCU.class,ClassUtil.getClassLowerName(BcuDataDicBCU.class));
    }
}
