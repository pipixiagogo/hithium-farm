package com.bugull.hithiumfarmweb.http.dao;


import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SysUserDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public boolean exists(Query existsQuery) {
        return mongoTemplate.exists(existsQuery,SysUser.class, ClassUtil.getClassLowerName(SysUser.class));
    }

    public void saveUser(SysUser sysUser) {
        mongoTemplate.insert(sysUser,ClassUtil.getClassLowerName(SysUser.class));
    }

    public SysUser findUser(Query findQuery) {
        return mongoTemplate.findOne(findQuery,SysUser.class,ClassUtil.getClassLowerName(SysUser.class));
    }

    public List<SysUser> findBatchUser(Query batchUserQuery) {
        return mongoTemplate.find(batchUserQuery,SysUser.class,ClassUtil.getClassLowerName(SysUser.class));
    }

    public long countNumberUser(Query batchUserQuery) {
        return mongoTemplate.count(batchUserQuery,SysUser.class,ClassUtil.getClassLowerName(SysUser.class));
    }

    public void updateByQuery(Query updateQuery, Update sysUserUpdate) {
        mongoTemplate.updateFirst(updateQuery,sysUserUpdate,SysUser.class,ClassUtil.getClassLowerName(SysUser.class));
    }

    public void removeByQuery(Query query) {
        mongoTemplate.remove(query,SysUser.class,ClassUtil.getClassLowerName(SysUser.class));
    }
}
