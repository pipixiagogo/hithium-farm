package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.BamsDataDicBA;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class BamsDataDicBADao {

    @Resource
    private MongoTemplate mongoTemplate;

    public BamsDataDicBA aggregationResult(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, ClassUtil.getClassLowerName(BamsDataDicBA.class), BamsDataDicBA.class).getMappedResults().get(0);
    }

    public BamsDataDicBA findBamData(Query bamQuery) {
        return mongoTemplate.findOne(bamQuery,BamsDataDicBA.class,ClassUtil.getClassLowerName(BamsDataDicBA.class));
    }

    public List<BamsDataDicBA> findBatchBamData(Query bamDataQuery) {
        return mongoTemplate.find(bamDataQuery,BamsDataDicBA.class,ClassUtil.getClassLowerName(BamsDataDicBA.class));
    }

    public long count(Query bamDataQuery) {
        return mongoTemplate.count(bamDataQuery,BamsDataDicBA.class,ClassUtil.getClassLowerName(BamsDataDicBA.class));
    }
}
