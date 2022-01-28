package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.AmmeterDataDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AmmeterDataDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<AmmeterDataDic> findBatchAmmeter(Query ammeterQuery) {
        return mongoTemplate.find(ammeterQuery,AmmeterDataDic.class, ClassUtil.getClassLowerName(AmmeterDataDic.class));
    }

    public long count(Query ammeterQuery) {
        return mongoTemplate.count(ammeterQuery,AmmeterDataDic.class,ClassUtil.getClassLowerName(AmmeterDataDic.class));
    }

    public Object findAmmeterQuery(Query query) {
        return mongoTemplate.findOne(query,AmmeterDataDic.class,ClassUtil.getClassLowerName(AmmeterDataDic.class));
    }
}
