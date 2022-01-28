package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class EquipmentDao {
    @Resource
    private MongoTemplate mongoTemplate;

    public List<Equipment> findBatchEquipment(Query findBatchQuery) {
        return mongoTemplate.find(findBatchQuery, Equipment.class, ClassUtil.getClassLowerName(Equipment.class));
    }

    public long count(Query findBatchQuery) {
        return mongoTemplate.count(findBatchQuery,Equipment.class, ClassUtil.getClassLowerName(Equipment.class));
    }
}
