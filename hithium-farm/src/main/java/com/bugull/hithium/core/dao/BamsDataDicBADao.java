package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.BamsDataDicBA;
import com.mongodb.Mongo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Locale;

@Repository
public class BamsDataDicBADao  {

    @Resource
    private MongoTemplate mongoTemplate;

    public void saveBamDataDic(BamsDataDicBA bamsDataDicBA) {
        mongoTemplate.insert(bamsDataDicBA,BamsDataDicBA.class.getSimpleName().toLowerCase(Locale.ROOT));
    }

    public BamsDataDicBA aggregationBamData(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation,BamsDataDicBA.class.getSimpleName().toLowerCase(Locale.ROOT),BamsDataDicBA.class).getMappedResults().get(0);
    }
}
