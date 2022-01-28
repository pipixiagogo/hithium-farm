package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.PcsChannelDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class PcsChannelDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;

    public PcsChannelDic findPcsChannel(Query query) {
        return mongoTemplate.findOne(query,PcsChannelDic.class,ClassUtil.getClassLowerName(PcsChannelDic.class));
    }

    public List<PcsChannelDic> findBatchPcsChannel(Query pcsChannelQuery) {
        return mongoTemplate.find(pcsChannelQuery,PcsChannelDic.class,ClassUtil.getClassLowerName(PcsChannelDic.class));
    }

    public long count(Query pcsChannelQuery) {
        return mongoTemplate.count(pcsChannelQuery,PcsChannelDic.class,ClassUtil.getClassLowerName(PcsChannelDic.class));
    }
}
