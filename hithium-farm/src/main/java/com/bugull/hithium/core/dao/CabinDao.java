package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Cabin;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CabinDao{
    @Resource
    private MongoTemplate mongoTemplate;
    public boolean exists(Query cabinQuery) {
        return mongoTemplate.exists(cabinQuery, ClassUtil.getClassLowerName(Cabin.class));
    }

    public void updateByQuery(Query cabinQuery, Update update) {
        mongoTemplate.updateMulti(cabinQuery,update,ClassUtil.getClassLowerName(Cabin.class));
    }

    public void saveBatchCabin(List<Cabin> cabinList) {
        mongoTemplate.insert(cabinList,ClassUtil.getClassLowerName(Cabin.class));
    }
}
