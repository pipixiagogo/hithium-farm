package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BmsCellTempDataDic;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class BmsCellTempDataDicDao {

    @Resource
    private MongoTemplate mongoTemplate;
    public void saveCellTempData(BmsCellTempDataDic tempDataDic) {
        mongoTemplate.insert(tempDataDic, ClassUtil.getClassLowerName(BmsCellTempDataDic.class));
    }
}
