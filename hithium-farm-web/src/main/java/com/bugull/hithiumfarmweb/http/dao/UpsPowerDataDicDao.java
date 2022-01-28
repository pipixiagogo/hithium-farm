package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.UpsPowerDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UpsPowerDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<UpsPowerDataDic> findBatchUpsPower(Query query) {
       return mongoTemplate.find(query,UpsPowerDataDic.class, ClassUtil.getClassLowerName(UpsPowerDataDic.class));
    }

    public long count(Query upsPowerQuery) {
        return mongoTemplate.count(upsPowerQuery,UpsPowerDataDic.class, ClassUtil.getClassLowerName(UpsPowerDataDic.class));
    }

    public Object findUpsPower(Query query) {
        return mongoTemplate.findOne(query,UpsPowerDataDic.class,ClassUtil.getClassLowerName(UpsPowerDataDic.class));
    }
}
