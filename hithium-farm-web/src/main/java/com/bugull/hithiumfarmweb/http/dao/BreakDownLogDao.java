package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BreakDownLogDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public List<BreakDownLog> findBatchBreakDownLogs(Query breakDownQuery) {
        return mongoTemplate.find(breakDownQuery,BreakDownLog.class, ClassUtil.getClassLowerName(BreakDownLog.class));
    }

    public Long countNumber(Query breakDownQuery) {
        return  mongoTemplate.count(breakDownQuery, BreakDownLog.class, ClassUtil.getClassLowerName(BreakDownLog.class));
    }

    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation,ClassUtil.getClassLowerName(BreakDownLog.class),BasicDBObject.class).getMappedResults();
    }
}
