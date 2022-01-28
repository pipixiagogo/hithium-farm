package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.PowerInstructions;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class PowerInstructionsDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void updateQuery(Query updateQuery, Update update) {
        mongoTemplate.updateMulti(updateQuery,update, PowerInstructions.class, ClassUtil.getClassLowerName(PowerInstructions.class));
    }

    public boolean exists(Query existsQuery) {
        return mongoTemplate.exists(existsQuery,PowerInstructions.class,ClassUtil.getClassLowerName(PowerInstructions.class));
    }

    public PowerInstructions findPower(Query existsQuery) {
        return mongoTemplate.findOne(existsQuery,PowerInstructions.class,ClassUtil.getClassLowerName(PowerInstructions.class));
    }

    public void save(PowerInstructions powerInstructions) {
        mongoTemplate.insert(powerInstructions,ClassUtil.getClassLowerName(PowerInstructions.class));
    }

    public PowerInstructions findAndRemove(Query findAndRemoveQuery) {
        return mongoTemplate.findAndRemove(findAndRemoveQuery,PowerInstructions.class,ClassUtil.getClassLowerName(PowerInstructions.class));
    }
}
