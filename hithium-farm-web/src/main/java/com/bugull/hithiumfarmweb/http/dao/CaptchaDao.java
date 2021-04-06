package com.bugull.hithiumfarmweb.http.dao;

import com.bugull.hithiumfarmweb.http.entity.Captcha;
import com.bugull.mongo.BuguDao;
import org.springframework.stereotype.Repository;

@Repository
public class CaptchaDao extends BuguDao<Captcha> {
    public CaptchaDao() {
        super(Captcha.class);
    }
}
