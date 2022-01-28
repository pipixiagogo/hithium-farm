package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.FireControlDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FireControlDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<FireControlDataDic> findBatchFireControl(Query fireControlQuery) {
        return mongoTemplate.find(fireControlQuery,FireControlDataDic.class, ClassUtil.getClassLowerName(FireControlDataDic.class));
    }

    public long count(Query fireControlQuery) {
        return mongoTemplate.count(fireControlQuery,FireControlDataDic.class, ClassUtil.getClassLowerName(FireControlDataDic.class));
    }

    public Object findFireControlQuery(Query fireControlQuery) {
        return mongoTemplate.findOne(fireControlQuery,FireControlDataDic.class,ClassUtil.getClassLowerName(FireControlDataDic.class));
    }
}
