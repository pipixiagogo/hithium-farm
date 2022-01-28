package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BamsDischargeCapacity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;


@Repository
public class BamsDischargeCapacityDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(BamsDischargeCapacity.class),BasicDBObject.class).getMappedResults();
    }
}
