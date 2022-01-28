package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.RemovePlanCurve;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class RemovePlanCurveDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public boolean exists(Query existsQuery) {
        return mongoTemplate.exists(existsQuery, RemovePlanCurve.class, ClassUtil.getClassLowerName(RemovePlanCurve.class));
    }

    public void save(RemovePlanCurve removePlanCurve) {
        mongoTemplate.insert(removePlanCurve,ClassUtil.getClassLowerName(RemovePlanCurve.class));
    }
}
