package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.dao.CorpDao;
import com.bugull.hithiumfarmweb.http.dao.PowerInstructionsDao;
import com.bugull.hithiumfarmweb.http.dao.RemovePlanCurveDao;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.Corp;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.CorpResponse;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.PowerInstructions;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.RemovePlanCurve;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.SecretUtil;
import com.bugull.hithiumfarmweb.utils.UUIDUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

import static com.bugull.hithiumfarmweb.common.Const.CROP_KEY;
import static com.bugull.hithiumfarmweb.common.Const.DEVICE_NAME;
import static com.mongodb.client.model.Updates.set;

@Service
public class ThirdPartyService {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Resource
    private RedisPoolUtil redisPoolUtil;
    @Resource
    private CorpDao corpDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private PowerInstructionsDao powerInstructionsDao;
    @Resource
    private RemovePlanCurveDao removePlanCurveDao;
    @Value("${spring.mail.username}")
    private String from;

    public ThirdPartyService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    /**
     * @param toMailReceive ????????????????????????
     * @param subject       ????????????
     * @param text          ??????????????????
     * @return ????????????????????????
     */
    public boolean sendEmailWithoutAttachment(String[] toMailReceive, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //??????
            mineHelper.setFrom(from);
            //????????????
            mineHelper.setTo(toMailReceive);
            //????????????
            mineHelper.setSubject(subject);
            //????????????   true ?????????????????????html
            mineHelper.setText(text, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException messagingException) {
            log.error("??????????????????:{}");
            return false;
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param toMailReceive ????????????????????????
     * @param subject       ????????????
     * @return ????????????????????????
     */
    public boolean sendTemplateEmailWithAttachment(String[] toMailReceive, String subject, Map<String, FileSystemResource> attachmentMap) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //??????
            mineHelper.setFrom(from);
            //????????????
            mineHelper.setTo(toMailReceive);
            //????????????
            mineHelper.setSubject(subject);
            //????????????   true ?????????????????????html
            Context context = new Context();
            context.setVariable("github_url", "https://github.com/Folgerjun");
            context.setVariable("blog_url", "http://putop.top/");
            String emailContent = templateEngine.process("mailTemplate", context);
            mineHelper.setText(emailContent, true);
            if (!attachmentMap.isEmpty()) {
                Set<Map.Entry<String, FileSystemResource>> entries = attachmentMap.entrySet();
                for (Map.Entry<String, FileSystemResource> entry : entries) {
                    mineHelper.addAttachment(entry.getKey(), entry.getValue());
                }
            }
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException messagingException) {
            log.error("??????????????????:{}");
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String[] toMailReceive, String subject, String text, Map<String, FileSystemResource> attachmentMap) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //??????
            mineHelper.setFrom(from);
            //????????????
            mineHelper.setTo(toMailReceive);
            //????????????
            mineHelper.setSubject(subject);
            //????????????   true ?????????????????????html
            mineHelper.setText(text, true);
            if (!attachmentMap.isEmpty()) {
                Set<Map.Entry<String, FileSystemResource>> entries = attachmentMap.entrySet();
                for (Map.Entry<String, FileSystemResource> entry : entries) {
                    mineHelper.addAttachment(entry.getKey(), entry.getValue());
                }
            }
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException messagingException) {
            log.error("??????????????????:{}");
            return false;
        }
    }

    public Map<String, Object> createAccessToken(String corpAccessSecret, String corpAccessId) {
        Map<String, Object> result = new HashMap<>();
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String corpKey = CROP_KEY + "_" + corpAccessSecret;
            String corpValue = jedis.get(corpKey);
            if (!StringUtils.isEmpty(corpValue)) {
                result.put("expiresTime", jedis.ttl(corpKey));
                result.put("accessToken", corpValue);
                return result;
            }
            Query corpQuery = new Query();
            corpQuery.addCriteria(Criteria.where("corpAccessId").is(corpAccessId)).addCriteria(Criteria.where("corpAccessSecret").is(corpAccessSecret));
            Corp corp = corpDao.findCorp(corpQuery);
            if (corp != null) {
                String content = System.currentTimeMillis() + "-" + corpAccessSecret + "-" + UUIDUtil.get32Str();
                String secretKey = propertiesConfig.getSecretKey();
                String accessToken = SecretUtil.encryptWithBase64(content, secretKey);
                result.put("expiresTime", 7200);
                result.put("accessToken", accessToken);
                jedis.setex(corpKey, 7200, accessToken);
                return result;
            }
            result.put("accessToken", null);
            return result;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    public boolean verifyAccessToken(String accessToken) {
        try {
            String replaceAccessToken = accessToken.replaceAll(" ", "+");
            String decryptWithBase64 = SecretUtil.decryptWithBase64(replaceAccessToken, propertiesConfig.getSecretKey());
            String[] baseSplit = decryptWithBase64.split("-");
            String corpSecret = baseSplit[1];
            return getRedisByCorpSecret(replaceAccessToken, corpSecret);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean getRedisByCorpSecret(String replaceAccessToken, String corpSecret) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String cropValue = jedis.get(CROP_KEY + "_" + corpSecret);
            if (Objects.equals(replaceAccessToken, cropValue)) {
                return true;
            }
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public CorpResponse setUpPlanCurve(PowerInstructions powerInstructions) {
        if (powerInstructions.getStrategyTime() != null) {
            powerInstructions.setType(1);
        } else {
            powerInstructions.setType(0);
        }
        powerInstructions.setAccessToken(null);
        Criteria criteria = new Criteria();
        if (powerInstructions.getType() == 0) {
            criteria.and(DEVICE_NAME).is( powerInstructions.getDeviceName()).and("type").is(powerInstructions.getType());
        } else {
            criteria.and(DEVICE_NAME).is( powerInstructions.getDeviceName()).and("strategyTime").is(powerInstructions.getStrategyTime()).and("type").is(powerInstructions.getType());
        }
        boolean flag = false;
        Query existsQuery = new Query();
        if (powerInstructionsDao.exists(existsQuery.addCriteria(criteria))) {
            PowerInstructions instructions =  powerInstructionsDao.findPower(existsQuery);
            Update powerUpdate = new Update();
            powerUpdate.set("orderId", powerInstructions.getOrderId())
                    .set("orderTime", powerInstructions.getOrderTime());
            if (!instructions.getPowerStrategy().equals(powerInstructions.getPowerStrategy())) {
                powerUpdate.set("powerStrategy", powerInstructions.getPowerStrategy())
                        .set("settingResult", powerInstructions.getSettingResult());
                flag = true;
            }
            powerInstructionsDao.updateQuery(existsQuery,powerUpdate);
        } else {
            powerInstructionsDao.save(powerInstructions);
        }
        JSONObject resultObject = new JSONObject();
        resultObject.put("orderId", powerInstructions.getOrderId());
        resultObject.put("deviceName", powerInstructions.getDeviceName());
        resultObject.put("strategyTime", powerInstructions.getStrategyTime());
        resultObject.put("powerStrategy", powerInstructions.getPowerStrategy());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultObject);
        if (flag) {
            System.out.println("????????????");
            /**
             * TODO ??????????????????
             */
        }
        return CorpResponse.ok(resultMap);
    }

    public boolean verifyOrderId(PowerInstructions powerInstructions) {
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("orderId").is(powerInstructions.getOrderId()))
                .addCriteria(Criteria.where(DEVICE_NAME).is(powerInstructions.getDeviceName()));
        return powerInstructionsDao.exists(existsQuery);
    }

    public boolean verifyDeviceName(String accessToken, String deviceName) {
        String corpSecret = SecretUtil.decryptWithBase64(accessToken.replaceAll(" ", "+"), propertiesConfig.getSecretKey()).split("-")[1];
        Query corpQuery = new Query();
        corpQuery.addCriteria(Criteria.where("corpAccessSecret").is(corpSecret));
        Corp corp = corpDao.findCorp(corpQuery);
        if (corp.getDeviceName().contains(deviceName)) {
            return true;
        }
        return false;
    }

    public boolean verifyRemoveOrderId(RemovePlanCurve removePlanCurve) {
        Query existsQuery = new Query();
        existsQuery.addCriteria(Criteria.where("orderId").is(removePlanCurve.getOrderId()))
                .addCriteria(Criteria.where(DEVICE_NAME).is(removePlanCurve.getDeviceName()));
        return removePlanCurveDao.exists(existsQuery);
    }

    public CorpResponse deletePlanCurve(RemovePlanCurve removePlanCurve) {
        PowerInstructions powerInstructions;
        removePlanCurve.setAccessToken(null);
        try {
            if (removePlanCurve.getStrategyTime() != null) {
                removePlanCurve.setType(1);
                Query findAndRemoveQuery = new Query();
                findAndRemoveQuery.addCriteria(Criteria.where(DEVICE_NAME).is(removePlanCurve.getDeviceName()))
                        .addCriteria(Criteria.where("strategyTime").is(removePlanCurve.getStrategyTime())).addCriteria(Criteria.where("type").is(1));
                powerInstructions=powerInstructionsDao.findAndRemove(findAndRemoveQuery);
            } else {
                removePlanCurve.setType(0);
                Query findAndRemoveQuery = new Query();
                findAndRemoveQuery.addCriteria(Criteria.where(DEVICE_NAME).is(removePlanCurve.getDeviceName())).addCriteria(Criteria.where("type").is(0));
                powerInstructions = powerInstructionsDao.findAndRemove(findAndRemoveQuery);
            }

            /**
             * TODO ????????????????????????
             */
            if (powerInstructions != null) {
                removePlanCurveDao.save(removePlanCurve);
            } else {
                if (removePlanCurve.getType() == 0) {
                    return CorpResponse.error(1, "???????????????????????????,????????????");
                }
                return CorpResponse.error(1, "?????????" + DateUtils.dateToStr(removePlanCurve.getStrategyTime()) + "????????????,????????????");
            }
            JSONObject resultObject = new JSONObject();
            resultObject.put("orderId", removePlanCurve.getOrderId());
            resultObject.put("deviceName", removePlanCurve.getDeviceName());
            resultObject.put("strategyTime", removePlanCurve.getStrategyTime());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", resultObject);
            return CorpResponse.ok(resultMap);
        } catch (Exception e) {
            return CorpResponse.error(1, "????????????????????????");
        }

    }


    public CorpResponse readPlanCurve(String deviceName, String orderId, String strategyTime) {
        JSONObject resultObject = new JSONObject();
        resultObject.put("orderId", orderId);
        resultObject.put("deviceName", deviceName);
        resultObject.put("strategyTime", strategyTime);
        if (StringUtils.isEmpty(strategyTime)) {
            Query powerQuery = new Query();
            powerQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("type").is(0));
            PowerInstructions powerInstructions = powerInstructionsDao.findPower(powerQuery);
            if (powerInstructions == null) {
                return CorpResponse.error(1, "???????????????????????????");
            }
            resultObject.put("powerStrategy", powerInstructions.getPowerStrategy());
            resultObject.put("settingResult", powerInstructions.getSettingResult());
        } else {
            try {
                Date strategyDate = DateUtils.strToDateYYYY(strategyTime);
                Query findQuery = new Query();
                findQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("strategyTime").is(strategyDate))
                        .addCriteria(Criteria.where("type").is(1));
                PowerInstructions powerInstructions = powerInstructionsDao.findPower(findQuery);
                if (powerInstructions == null) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).addCriteria(Criteria.where("type").is(0));
                    powerInstructions = powerInstructionsDao.findPower(query);
                    if (powerInstructions == null) {
                        return CorpResponse.error(1, "???????????????????????????");
                    }
                }
                resultObject.put("powerStrategy", powerInstructions.getPowerStrategy());
                resultObject.put("settingResult", powerInstructions.getSettingResult());
            } catch (Exception e) {
                return CorpResponse.error(0, "????????????");
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultObject);
        return CorpResponse.ok(resultMap);
    }

    public boolean verifyPlanCurve(PowerInstructions powerInstructions) {
       return powerInstructionsDao.exists(new Query().addCriteria(Criteria.where("orderId").is(powerInstructions.getOrderId())).addCriteria(Criteria.where(DEVICE_NAME).is(powerInstructions.getDeviceName())));
    }
}
