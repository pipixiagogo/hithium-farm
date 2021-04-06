package com.bugull.hithiumfarmweb.http.service;


import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.http.dao.CaptchaDao;
import com.bugull.hithiumfarmweb.http.entity.Captcha;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        captchaDao.remove(captchaDao.query().lessThan("expireTime",new Date()).pageNumber(20).pageSize(1));
        Captcha captcha = captchaDao.query().is("uuid", uuid).result();
        if(captcha != null){
            captchaDao.update().set("expireTime",dateMinutes).execute(captcha);
            return producer.createImage(captcha.getCode());
        }else {
            //生成文字验证码
            String code = producer.createText();
            Captcha captchaEntity = new Captcha();
            captchaEntity.setUuid(uuid);
            captchaEntity.setCode(code);
            //5分钟后过期
            captchaEntity.setExpireTime(dateMinutes);
            captchaDao.insert(captchaEntity);
            /**
             * 删除过期的验证码 一次删符合条件的20条
             */
            return producer.createImage(code);
        }
    }

    public Captcha validaCaptcha( String uuid) {
        Captcha captchaEntity = captchaDao.query().is("uuid", uuid).result();

        captchaDao.remove(captchaDao.query().lessThan("expireTime",new Date()).pageNumber(20).pageSize(1));
        return captchaEntity;
    }
}
