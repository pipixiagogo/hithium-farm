package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.ImgUpload;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ImgUploadsDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public void saveImgUploads(List<ImgUpload> imgUploads) {
        mongoTemplate.insert(imgUploads, ClassUtil.getClassLowerName(ImgUpload.class));
    }

    public List<ImgUpload> findBatchImgUploads(Query query) {
        return mongoTemplate.find(query,ImgUpload.class,ClassUtil.getClassLowerName(ImgUpload.class));
    }

    public void removeBatch(Query query) {
        mongoTemplate.remove(query,ImgUpload.class,ClassUtil.getClassLowerName(ImgUpload.class));
    }
}
