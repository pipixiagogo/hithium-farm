package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Cube;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CubeDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public Cube findCube(Query cubeQuery) {
        return mongoTemplate.findOne(cubeQuery,Cube.class, ClassUtil.getClassLowerName(Cube.class));
    }
}
