package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.PcsChannelDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class PcsChannelDicDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public void savePcsChannel(PcsChannelDic pcsChannelDic) {
        mongoTemplate.insert(pcsChannelDic, ClassUtil.getClassLowerName(PcsChannelDic.class));
    }
}
