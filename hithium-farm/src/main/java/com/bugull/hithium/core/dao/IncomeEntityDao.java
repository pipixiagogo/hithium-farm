package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.IncomeEntity;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class IncomeEntityDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public boolean existQuery(Query query) {
        return mongoTemplate.exists(query, ClassUtil.getClassLowerName(IncomeEntity.class));
    }

    public IncomeEntity findIncomeEntity(Query query) {
        return mongoTemplate.findOne(query, IncomeEntity.class, ClassUtil.getClassLowerName(IncomeEntity.class));
    }

    public void updateQuery(Query query, Update update) {
        mongoTemplate.updateFirst(query,update, ClassUtil.getClassLowerName(IncomeEntity.class));
    }


    public void saveIncomeEntity(IncomeEntity incomeEntity) {
        mongoTemplate.insert(incomeEntity, ClassUtil.getClassLowerName(IncomeEntity.class));
    }
}
