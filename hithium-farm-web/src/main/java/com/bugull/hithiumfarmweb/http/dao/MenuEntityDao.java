package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.MenuEntity;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MenuEntityDao {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<MenuEntity> findMenu(Query query) {
            return mongoTemplate.find(query,MenuEntity.class, ClassUtil.getClassLowerName(MenuEntity.class));
    }
}
