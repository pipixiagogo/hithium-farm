package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Cube;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CubeDao {
    @Resource
    private MongoTemplate mongoTemplate;
    public Cube findCube(Query query) {
        return mongoTemplate.findOne(query, Cube.class,ClassUtil.getClassLowerName(Cube.class));
    }

    public boolean exists(Query query) {
        return mongoTemplate.exists(query,Cube.class,ClassUtil.getClassLowerName(Cube.class));
    }

    public void updateByQuery(Query query, Update update) {
        mongoTemplate.updateMulti(query,update,ClassUtil.getClassLowerName(Cube.class));
    }

    public void saveCube(Cube cube) {
        mongoTemplate.insert(cube,ClassUtil.getClassLowerName(Cube.class));
    }
}
