package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BreakDownLog;
import com.bugull.hithium.core.util.ClassUtil;
import com.mongodb.Mongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BreakDownLogDao {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveBatchBreakDown(List<BreakDownLog> breakDownLogList) {
        mongoTemplate.insert(breakDownLogList, ClassUtil.getClassLowerName(BreakDownLog.class));
    }

    public void updateByQuery(Query breakDownLogQuery, Update update) {
        mongoTemplate.updateMulti(breakDownLogQuery,update,ClassUtil.getClassLowerName(BreakDownLog.class));
    }
}
