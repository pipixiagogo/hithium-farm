package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.RoleEntity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class RoleEntityDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<RoleEntity> findBatch(Query roleBatch) {
        return mongoTemplate.find(roleBatch,RoleEntity.class, ClassUtil.getClassLowerName(RoleEntity.class));
    }

    public RoleEntity findRole(Query query) {
        return mongoTemplate.findOne(query,RoleEntity.class, ClassUtil.getClassLowerName(RoleEntity.class));
    }
}
