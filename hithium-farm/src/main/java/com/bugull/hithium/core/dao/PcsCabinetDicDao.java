package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.PcsCabinetDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class PcsCabinetDicDao  {

    @Resource
    private MongoTemplate mongoTemplate;
    public void savePcsCabinetDic(PcsCabinetDic pcsCabinetDic) {
        mongoTemplate.insert(pcsCabinetDic, ClassUtil.getClassLowerName(PcsCabinetDic.class));
    }
}
