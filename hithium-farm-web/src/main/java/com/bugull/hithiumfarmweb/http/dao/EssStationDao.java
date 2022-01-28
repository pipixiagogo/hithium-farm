package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class EssStationDao {
    @Resource
    private MongoTemplate mongoTemplate;
    public boolean exists(Query query) {
        return mongoTemplate.exists(query,EssStation.class, ClassUtil.getClassLowerName(EssStation.class));
    }

    public List<EssStation> findEssStationBatch(Query essStationQuery) {
        return mongoTemplate.find(essStationQuery,EssStation.class,ClassUtil.getClassLowerName(EssStation.class));
    }


    public void remove(Query removeQuery) {
        mongoTemplate.remove(removeQuery,EssStation.class,ClassUtil.getClassLowerName(EssStation.class));
    }

    public EssStation findEssStation(Query findQuery) {
        return mongoTemplate.findOne(findQuery,EssStation.class,ClassUtil.getClassLowerName(EssStation.class));
    }

    public long countQuery(Query findEssStationQuery) {
        return mongoTemplate.count(findEssStationQuery,EssStation.class,ClassUtil.getClassLowerName(EssStation.class));
    }

    public void saveEssStation(EssStation essStation) {
        mongoTemplate.insert(essStation,ClassUtil.getClassLowerName(EssStation.class));
    }

    public void updateByQuery(Query query, Update essStationUpdate) {
        mongoTemplate.updateFirst(query,essStationUpdate,EssStation.class,ClassUtil.getClassLowerName(EssStation.class));
    }

    public List<BasicDBObject> aggregation(Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation,ClassUtil.getClassLowerName(EssStation.class),BasicDBObject.class).getMappedResults();
    }
}
