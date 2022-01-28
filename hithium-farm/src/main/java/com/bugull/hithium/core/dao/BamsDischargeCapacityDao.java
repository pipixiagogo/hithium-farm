package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BamsDischargeCapacity;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class BamsDischargeCapacityDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public void saveBamDischargeCapacity(BamsDischargeCapacity bamsDischargeCapacity) {
        mongoTemplate.insert(bamsDischargeCapacity, ClassUtil.getClassLowerName(BamsDischargeCapacity.class));
    }

    public BamsDischargeCapacity aggregateDischargeCapacityData(Aggregation dischargeCapacityAggregation) {
        return mongoTemplate.aggregate(dischargeCapacityAggregation,ClassUtil.getClassLowerName(BamsDischargeCapacity.class),BamsDischargeCapacity.class).getMappedResults().get(0);
    }

}
