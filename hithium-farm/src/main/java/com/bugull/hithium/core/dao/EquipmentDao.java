package com.bugull.hithium.core.dao;

import com.bugull.hithium.core.entity.Equipment;
import com.bugull.hithium.core.util.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

@Repository
public class EquipmentDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public List<Equipment> queryEquipment(Query query){
        return mongoTemplate.find(query,Equipment.class,ClassUtil.getClassLowerName(Equipment.class));
    }

    public void saveBatchEquipment(List<Equipment> equipmentList) {
        mongoTemplate.insert(equipmentList, ClassUtil.getClassLowerName(Equipment.class));
    }

    public void removeByQuery(Query query) {
        mongoTemplate.remove(query, ClassUtil.getClassLowerName(Equipment.class));
    }

    public void updateByQuery(Query updateQuery, Update update) {
        mongoTemplate.updateMulti(updateQuery,update, ClassUtil.getClassLowerName(Equipment.class));
    }
}
