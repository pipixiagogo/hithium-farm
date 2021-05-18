package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.ExportRecord;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class ExportRecordDao extends BuguDao<ExportRecord> {
    public ExportRecordDao() {
        super(ExportRecord.class);
    }
}
