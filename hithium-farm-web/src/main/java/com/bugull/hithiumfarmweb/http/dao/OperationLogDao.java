package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.OperationLog;
import com.bugull.hithiumfarmweb.http.entity.OperationLogEntity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OperationLogDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<OperationLogEntity> findBatch(Query batchQuery) {
        return mongoTemplate.find(batchQuery,OperationLogEntity.class, ClassUtil.getClassLowerName(OperationLog.class));
    }

    public long count(Query batchQuery) {
        return mongoTemplate.count(batchQuery,OperationLogEntity.class, ClassUtil.getClassLowerName(OperationLog.class));
    }

    public void saveOperationLog(OperationLog operationLog) {
        mongoTemplate.insert(operationLog,ClassUtil.getClassLowerName(OperationLog.class));
    }
}
