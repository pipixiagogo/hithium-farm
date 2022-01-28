package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Pccs;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class PccsDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public Pccs findPccs(Query query) {
       return mongoTemplate.findOne(query, Pccs.class, ClassUtil.getClassLowerName(Pccs.class));

    }

    public void updateByQuery(Query query, Update update) {
        mongoTemplate.updateMulti(query,update,ClassUtil.getClassLowerName(Pccs.class));
    }

    public void savePccs(Pccs pccs) {
        mongoTemplate.insert(pccs);
    }
}
