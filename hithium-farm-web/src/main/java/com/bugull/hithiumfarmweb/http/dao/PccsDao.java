package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Pccs;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class PccsDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<Pccs> findBatchPccs(Query pccsQuery) {
        return mongoTemplate.find(pccsQuery,Pccs.class, ClassUtil.getClassLowerName(Pccs.class));
    }
}
