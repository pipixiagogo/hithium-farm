package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.IncomeEntity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class IncomeEntityDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public IncomeEntity findIncome(Query incomeQuery) {
        return mongoTemplate.findOne(incomeQuery,IncomeEntity.class, ClassUtil.getClassLowerName(IncomeEntity.class));
    }



    public List<BasicDBObject> aggregate(Aggregation aggregation){
        return mongoTemplate.aggregate(aggregation,ClassUtil.getClassLowerName(IncomeEntity.class),BasicDBObject.class).getMappedResults();
    }

    public List<IncomeEntity> findBatchIncome(Query incomeQuery) {
        return mongoTemplate.find(incomeQuery,IncomeEntity.class,ClassUtil.getClassLowerName(IncomeEntity.class));
    }
}
