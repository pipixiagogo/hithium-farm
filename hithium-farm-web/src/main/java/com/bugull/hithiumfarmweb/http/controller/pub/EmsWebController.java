package com.bugull.hithiumfarmweb.http.controller.pub;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.service.ThirdPartyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/EmsWebService")
public class EmsWebController {


    @Resource
    private RedisPoolUtil redisPoolUtil;
//    @Resource
//    private ThirdPartyService thirdPartyService;
//    private final TemplateEngine templateEngine;
//    private final JavaMailSender mailSender;
//    @Value("${spring.mail.username}")
//    private String from;
//
//    public EmsWebController(JavaMailSender mailSender, TemplateEngine templateEngine) {
//        this.mailSender = mailSender;
//        this.templateEngine = templateEngine;
//    }

//    @RequestMapping("/templateMail")
//    public String templateMail() {
//        try {
//            Context context = new Context();
//            context.setVariable("github_url", "https://github.com/Folgerjun");
//            context.setVariable("blog_url", "http://putop.top/");
//            String emailContent = templateEngine.process("mailTemplate", context);
//            Map<String, FileSystemResource> attachmentMap = new HashMap<>();
//            attachmentMap.put("img01.png", new FileSystemResource("D://微信截图_20210811093733.png"));
////            thirdPartyService.sendEmailWithAttachment(new String[]{"454092626@qq.com", "zhouh@hithium.cn"}, "这是模板文件", emailContent,attachmentMap );
//            thirdPartyService.sendTemplateEmailWithAttachment(new String[]{"454092626@qq.com", "zhouh@hithium.cn"}, "这是模板文件",attachmentMap );
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "发送失败";
//        }
//        return "发送成功";
//    }

//    @GetMapping(value = "/sendEmail")
//    public String sendEmail() throws Exception {
//        thirdPartyService.sendEmailWithoutAttachment(new String[]{"454092626@qq.com", "zhouh@hithium.cn"}, "测试2021-08-17", "操作一波");
//        Map<String, FileSystemResource> attachmentMap = new HashMap<>();
//        attachmentMap.put("img01.png",new FileSystemResource("D://微信截图_20210811093733.png"));
//        attachmentMap.put("excel.xlsx",new FileSystemResource("D://Users//zhouh//Desktop//电池堆数据.xlsx"));
//        thirdPartyService.sendEmailWithAttachment(new String[]{"454092626@qq.com", "zhouh@hithium.cn"}, "测试2021-08-17-2", "操作两波", attachmentMap);
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
//        //谁发
//        mineHelper.setFrom(from);
//        //谁要接收
//        mineHelper.setTo(new String[]{"454092626@qq.com","zhouh@hithium.cn"});
//        //邮件主题
//        mineHelper.setSubject("测试2021-08-11");
//        //邮件内容   true 表示带有附件或html
//        mineHelper.setText("你是真的皮", true);
//        mineHelper.addAttachment("img01.png",new FileSystemResource("D://微信截图_20210811093733.png"));
////        SimpleMailMessage message = new SimpleMailMessage();
////        message.setFrom(from);
////        message.setSubject("测试2021-08-10");
////        message.setText("发送邮件看一看");
////        /**
////         * 邮箱列表
////         */
////        message.setTo(new String[]{"454092626@qq.com","zhouh@hithium.cn"});
//        mailSender.send(mimeMessage);
//        return "OK";
//    }

    /**
     * 设备状态信息  需要有设备才有这些状态
     *
     * @param deviceName
     * @return
     */
    @ApiOperation(value = "获取设备状态信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetEquipmentRealTimeData")
    public JSONObject GetEquipmentRealTimeData(@RequestParam(value = "deviceName", required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String device = "REALDATA-" + deviceName;
            String realData = jedis.get(device);
            if (!StringUtils.isEmpty(realData)) {
                JSONObject realDataJson = JSONObject.parseObject(realData);
                if (realDataJson != null) {
                    return realDataJson;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            return jsonObject;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设备列表信息
     */
    @ApiOperation(value = "获取设备列表信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetAllEquipmentData")
    public JSONObject GetAllEquipmentData(@RequestParam(value = "deviceName", required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String device = "DEVICEINFO-" + deviceName;
            String deviceInfoStr = jedis.get(device);
            if (!StringUtils.isEmpty(deviceInfoStr)) {
                JSONObject deviceInfoJson = JSONObject.parseObject(deviceInfoStr);
                if (deviceInfoJson != null) {
                    return deviceInfoJson;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            return jsonObject;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 告警日志
     *
     * @param deviceName
     * @return
     */
    @ApiOperation("获取告警日志信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetAlarmRealTimeData")
    public JSONArray GetAlarmRealTimeData(@RequestParam(value = "deviceName", required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String breakDownLogStr = jedis.get("BREAKDOWNLOG-" + deviceName);
            if (!StringUtils.isEmpty(breakDownLogStr)) {
                JSONArray breakDownLogJson = JSONArray.parseArray(breakDownLogStr);
                if (!CollectionUtils.isEmpty(breakDownLogJson) && breakDownLogJson.size() > 0) {
                    return breakDownLogJson;
                }
            }
            JSONArray resultArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            resultArray.add(jsonObject);
            return resultArray;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
}
