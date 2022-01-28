package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.dao.CaptchaDao;
import com.bugull.hithiumfarmweb.http.entity.Captcha;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Date;

@Service
public class CaptchaService {

    @Resource
    private Producer producer;
    @Resource
    private CaptchaDao captchaDao;


    public BufferedImage getCaptcha(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            throw new ParamsValidateException("uuid不能为空");
        }
        Date dateMinutes = DateUtils.addDateMinutes(new Date(), 5);
        /**
         * 删除过期的验证码 一次删符合条件的20条
         * 修改重复ID生成统一验证码
         */
        Query removeQuery = new Query();
        removeQuery.addCriteria(Criteria.where("expireTime").lt(new Date()))
                .skip(0).limit(20);
        captchaDao.remove(removeQuery);
        Query findQuery=new Query();
        Captcha captcha =captchaDao.findCaptcha(findQuery);
        if (captcha != null) {
            Update update = new Update();
            update.set("expireTime", dateMinutes);
            captchaDao.updateByQuery(findQuery,update);
            return producer.createImage(captcha.getCode());
        } else {
            //生成文字验证码
            String code = producer.createText();
            Captcha captchaEntity = new Captcha();
            captchaEntity.setUuid(uuid);
            captchaEntity.setCode(code);
            //5分钟后过期
            captchaEntity.setExpireTime(dateMinutes);
            captchaDao.saveCaptcha(captchaEntity);
            return producer.createImage(code);
        }
    }

    public String validaCaptcha(LoginFormBo loginFormBo) {
        Query findQuery=new Query();
        findQuery.addCriteria(Criteria.where("uuid").is(loginFormBo.getUuid()));
        Captcha captchaEntity = captchaDao.findCaptcha(findQuery);
        if (captchaEntity == null) {
            return "登录失败,验证码错误";
        }
        if (captchaEntity != null && !captchaEntity.getCode().equalsIgnoreCase(loginFormBo.getCaptcha())) {
            return "登录失败,验证码错误";
        }
        if (System.currentTimeMillis() > captchaEntity.getExpireTime().getTime()) {
            return "登录失败,验证码过期";
        }
        return null;
    }
}
