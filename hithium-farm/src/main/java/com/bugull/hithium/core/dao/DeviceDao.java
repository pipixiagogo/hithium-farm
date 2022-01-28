package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Device;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class DeviceDao {

    @Resource
    private MongoTemplate mongoTemplate;
    public Device findDevice(Query query){
        return mongoTemplate.findOne(query,Device.class);
    }

    public void updateFirst(Query query, Update update) {
        mongoTemplate.updateFirst(query,update, ClassUtil.getClassLowerName(Device.class));
    }

    public boolean exists(Query query) {
        return mongoTemplate.exists(query, ClassUtil.getClassLowerName(Device.class));
    }

    public void saveDevice(Device device) {
        mongoTemplate.insert(device);
    }
}
