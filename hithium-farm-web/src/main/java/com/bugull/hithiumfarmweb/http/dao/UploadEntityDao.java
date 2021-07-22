package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.UploadEntity;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class UploadEntityDao extends BuguDao<UploadEntity> {
    public UploadEntityDao() {
        super(UploadEntity.class);
    }
}
