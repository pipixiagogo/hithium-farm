package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.ConnetDevice;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ConnetDeviceDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public boolean exists(Query existsQuery) {
      return  mongoTemplate.exists(existsQuery,ConnetDevice.class, ClassUtil.getClassLowerName(ConnetDevice.class));
    }

    public void saveConnetDevice(ConnetDevice connetDevice) {
        mongoTemplate.insert(connetDevice,ClassUtil.getClassLowerName(ConnetDevice.class));
    }

    public ConnetDevice findConnetDevice(Query findQuery) {
        return mongoTemplate.findOne(findQuery,ConnetDevice.class, ClassUtil.getClassLowerName(ConnetDevice.class));
    }
}
