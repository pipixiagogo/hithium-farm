package com.bugull.hithium.integration.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithium.integration.message.JMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;

@Component("unknowService")
public class UnknowService {
    private static final Logger log = LoggerFactory.getLogger(UnknowService.class);
    public static final String SQL_PREFIX = "$ESS/";
    public static final String S2D_SUFFIX = "/MSG/D2S";

    @Resource
    private MessageHandler mqttOutbound;

    public void handle(Message<JMessage> messageMessage) {
        JMessage jMessage = messageMessage.getPayload();
        MsgEntity parseObject = JSON.parseObject(jMessage.getPayloadMsg(), MsgEntity.class);
        log.info("未知数据主题:{},数据内容:{},时间:{}", jMessage.getTopic(), jMessage.getPayloadMsg(),parseObject.getDate());
    }

    //    @Scheduled(cron = "${energy.untrans.report.interval}")
    public void sendMqttMsg() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", "REAL_TIME_DATA");
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        jsonObject.put("DATA", "123");
        Message<String> message = MessageBuilder.withPayload(jsonObject.toString()).setHeader(MqttHeaders.TOPIC, getEssSendTopic("123"))
                .setHeader(MqttHeaders.QOS, 0)
                .build();
        mqttOutbound.handleMessage(message);
    }

    public static final String getEssSendTopic(String deviceName) {
        return SQL_PREFIX + deviceName + S2D_SUFFIX;
    }


}
