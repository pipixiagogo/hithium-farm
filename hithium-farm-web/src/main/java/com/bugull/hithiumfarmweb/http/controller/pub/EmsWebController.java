package com.bugull.hithiumfarmweb.http.controller.pub;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.controller.pub.entity.BcuEntity;
import com.bugull.hithiumfarmweb.http.controller.pub.entity.BcuEntityDao;
import com.bugull.hithiumfarmweb.http.dao.BcuDataDicBCUDao;
import com.bugull.hithiumfarmweb.http.entity.BcuDataDicBCU;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.HttpUtils;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/EmsWebService")
public class EmsWebController {

    private static final Logger log = LoggerFactory.getLogger(EmsWebController.class);
    @Resource
    private RedisPoolUtil redisPoolUtil;
    @Resource
    private BcuDataDicBCUDao bcuDataDicBCUDao;
    @Resource
    private BcuEntityDao bcuEntityDao;

    @Resource
    private PropertiesConfig propertiesConfig;

    /**
     * 发送短信  对接第三方接口
     * @return
     */
//    @RequestMapping(value = "/sendMsg")
//    public String sendMsg(){
////        AppCode：a6236cf2b04645baabb8dcc9c67771d5
//        Map<String, String> headers = new HashMap<String, String>();
//        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//        headers.put("Authorization", "APPCODE " + propertiesConfig.getSmsAppcode());
//        //根据API的要求，定义相对应的Content-Type
//        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//        Map<String, String> querys = new HashMap<String, String>();
//        Map<String, String> bodys = new HashMap<String, String>();
//        bodys.put("callbackUrl", propertiesConfig.getSmsCallbackUrl());
//        bodys.put("channel", "0");
//        bodys.put("mobile", "+8613215007077");
//        bodys.put("templateID", propertiesConfig.getSmsTemplateId());
//        bodys.put("templateParamSet", "测试, 1");
//        try {
//            /**
//             * 重要提示如下:
//             * HttpUtils请从
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
//             * 下载
//             *
//             * 相应的依赖请参照
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
//             */
//            HttpResponse response = HttpUtils.doPost(propertiesConfig.getSmsHost(), propertiesConfig.getSmsPath(), propertiesConfig.getSmsMethod(), headers, querys, bodys);
//            System.out.println(response.toString());
//            System.out.println("321"+ EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "OK";
//    }
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

    /**
     * 根据模板发送Email
     */
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

    @ApiOperation(value = "获取时间设备数据")
    @RequestMapping(method = RequestMethod.GET,value = "getDeviceTimeData")
    public ResHelper<JSONObject> getDeviceTimeData(@RequestParam(value = "deviceName",required = false)String deviceName,
                                       @RequestParam(value = "name",required = false)String name,
                                       @RequestParam(value = "time",required = false)String time){
        if(StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(time) || StringUtils.isEmpty(name)){
            return ResHelper.pamIll();
        }
        if (!Const.DATE_PATTERN.matcher(time).matches()) {
            return ResHelper.pamIll();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Date startTime = DateUtils.dateToStrWithHHmm(time);
            Date endTime = DateUtils.getEndTime(startTime);
            long start = System.currentTimeMillis();
            List<BcuEntity> bcuDataDicBCUS = bcuEntityDao.query().is("deviceName", deviceName).is("name", name)
                    .greaterThan("generationDataTime", startTime).lessThan("generationDataTime", endTime)
                    .results();
            long end = System.currentTimeMillis();
            log.info("导出耗时:{},条数:{}",(end-start),bcuDataDicBCUS.size());
            if(!CollectionUtils.isEmpty(bcuDataDicBCUS) && !bcuDataDicBCUS.isEmpty()){
                jsonObject.put("result",JSONObject.toJSON(bcuDataDicBCUS));
                return ResHelper.success("",jsonObject);
            }
        }catch (Exception e){
            log.info("解析日期出错:{}",e);
        }
        return ResHelper.success("数据为空",jsonObject);
    }


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
