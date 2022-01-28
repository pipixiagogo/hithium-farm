package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.Corp;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CorpDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public Corp findCorp(Query query) {
        return mongoTemplate.findOne(query,Corp.class, ClassUtil.getClassLowerName(Corp.class));
    }
}
