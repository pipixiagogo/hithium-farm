package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.DeviceAreaEntity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class DeviceDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public Device findDevice(Query query) {
        return mongoTemplate.findOne(query,Device.class, ClassUtil.getClassLowerName(Device.class));
    }

    public List<Device> findBatchDevices(Query query) {
        return mongoTemplate.find(query,Device.class,ClassUtil.getClassLowerName(Device.class));
    }

    public boolean exists(Query existsQuery) {
        return mongoTemplate.exists(existsQuery,Device.class,ClassUtil.getClassLowerName(Device.class));
    }

    public boolean updateByQuery(Query updateQuery, Update update) {
       return  mongoTemplate.updateMulti(updateQuery,update,Device.class,ClassUtil.getClassLowerName(Device.class)).getMatchedCount() > 0L;
    }

    public List<BasicDBObject> aggregationResult(Aggregation deviceAggregation) {
        return mongoTemplate.aggregate(deviceAggregation,Device.class,BasicDBObject.class).getMappedResults();
    }

    public List<DeviceAreaEntity> findBatchDevicesArea(Query deviceQuery) {
        return mongoTemplate.find(deviceQuery,DeviceAreaEntity.class,ClassUtil.getClassLowerName(Device.class));
    }

    public long count(Query deviceQuery) {
        return mongoTemplate.count(deviceQuery,Device.class,ClassUtil.getClassLowerName(Device.class));
    }

    public List<BasicDBObject> aggregation(Aggregation aggregation) {
      return   mongoTemplate.aggregate(aggregation,ClassUtil.getClassLowerName(Device.class), BasicDBObject.class).getMappedResults();
    }
}
