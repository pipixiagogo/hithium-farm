package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.ExportRecord;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ExportRecordDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public List<ExportRecord> findBatch(Query exportQuery) {
        return mongoTemplate.find(exportQuery,ExportRecord.class, ClassUtil.getClassLowerName(ExportRecord.class));
    }

    public boolean exists(Query query) {
        return mongoTemplate.exists(query,ExportRecord.class, ClassUtil.getClassLowerName(ExportRecord.class));
    }

    public void saveExportRecord(ExportRecord exportRecord) {
        mongoTemplate.insert(exportRecord,ClassUtil.getClassLowerName(ExportRecord.class));
    }

    public void removeQuery(Query exportQuery) {
        mongoTemplate.remove(exportQuery,ExportRecord.class,ClassUtil.getClassLowerName(ExportRecord.class));
    }
}
