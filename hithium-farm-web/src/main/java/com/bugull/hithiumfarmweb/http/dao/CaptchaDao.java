package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Captcha;
import com.bugull.hithiumfarmweb.utils.ClassUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CaptchaDao  {
    @Resource
    private MongoTemplate mongoTemplate;
    public void remove(Query query) {
        mongoTemplate.remove(query,Captcha.class, ClassUtil.getClassLowerName(Captcha.class));
    }


    public Captcha findCaptcha(Query findQuery) {
        return mongoTemplate.findOne(findQuery,Captcha.class, ClassUtil.getClassLowerName(Captcha.class));
    }

    public void updateByQuery(Query findQuery, Update update) {
        mongoTemplate.updateFirst(findQuery,update,Captcha.class, ClassUtil.getClassLowerName(Captcha.class));
    }

    public void saveCaptcha(Captcha captchaEntity) {
        mongoTemplate.insert(captchaEntity);
    }
}
