package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BmsCellTempDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BmsCellTempDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(BmsCellTempDataDic.class),BasicDBObject.class).getMappedResults();
    }

    public BmsCellTempDataDic findBmsCellTemp(Query query) {
        return mongoTemplate.findOne(query,BmsCellTempDataDic.class,ClassUtil.getClassLowerName(BmsCellTempDataDic.class));
    }

    public List<BmsCellTempDataDic> findBatchBmsCellTemp(Query query) {
        return mongoTemplate.find(query,BmsCellTempDataDic.class,ClassUtil.getClassLowerName(BmsCellTempDataDic.class));
    }

    public long count(Query query) {
        return mongoTemplate.count(query,BmsCellTempDataDic.class,ClassUtil.getClassLowerName(BmsCellTempDataDic.class));
    }
}
