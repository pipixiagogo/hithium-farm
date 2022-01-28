package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.PcsCabinetDic;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class PcsCabinetDicDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public PcsCabinetDic findPcsCabinet(Query pcsCabinetQuery) {
        return mongoTemplate.findOne(pcsCabinetQuery,PcsCabinetDic.class, ClassUtil.getClassLowerName(PcsCabinetDic.class));
    }

    public List<PcsCabinetDic> findBatchPcsCabinet(Query pcsCabinetQuery) {
        return mongoTemplate.find(pcsCabinetQuery,PcsCabinetDic.class,ClassUtil.getClassLowerName(PcsCabinetDic.class));
    }

    public long count(Query pcsCabinetQuery) {
        return mongoTemplate.count(pcsCabinetQuery,PcsCabinetDic.class,ClassUtil.getClassLowerName(PcsCabinetDic.class));
    }
}
