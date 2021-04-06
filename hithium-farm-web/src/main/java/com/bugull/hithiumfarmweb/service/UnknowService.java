package com.bugull.hithiumfarmweb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.message.JMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.UUID;

@Component("unknowService")
public class UnknowService {
    private static final Logger log = LoggerFactory.getLogger(UnknowService.class);
    public static final String SQL_PREFIX = "$ESS/";
    public static final String S2D_SUFFIX = "/MSG/D2S";

    @Resource
    private MessageHandler mqttOutbound;

    public void handle(Message<JMessage> messageMessage) {
        JMessage jMessage = messageMessage.getPayload();
        log.info("未知数据topic:{}",jMessage.getTopic());
    }
//    @Scheduled(cron = "${energy.untrans.report.interval}")
    public void sendMqttMsg(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", "REAL_TIME_DATA");
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        jsonObject1.put("pipixia", s);
        System.out.println(s);
        Message<String> message = MessageBuilder.withPayload(jsonObject.toString()).setHeader(MqttHeaders.TOPIC, getEssSendTopic("123"))
                .setHeader(MqttHeaders.QOS, 0)
                .build();
        mqttOutbound.handleMessage(message);
    }
    public static final String getEssSendTopic(String deviceName) {
        return SQL_PREFIX + deviceName + S2D_SUFFIX;
    }

//    @Scheduled(cron = "${energy.untrans.report.interval}")
    public void sendMqttMsg2(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", DataType.DEVICE_LIST_DATA);
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        jsonObject1.put("pipixia9999999999999999999", s);
        Message<String> message = MessageBuilder.withPayload(jsonObject.toString()).setHeader(MqttHeaders.TOPIC, getEssSendTopic("123"))
                .setHeader(MqttHeaders.QOS, 0)
                .build();
        mqttOutbound.handleMessage(message);
    }

//    @Scheduled(cron = "${energy.untrans.report.interval}")
    public void sendMqttMsg3(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", DataType.BREAKDOWNLOG_DATA);
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONArray jsonObject1 = new JSONArray();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        JSONObject jsonObject2 = new JSONObject();

        jsonObject1.add(jsonObject2);
        for(int i=0;i<10;i++){
            jsonObject2.put("pipi"+i,"xia"+i);
        }
        Message<String> message = MessageBuilder.withPayload(jsonObject.toString()).setHeader(MqttHeaders.TOPIC, getEssSendTopic("123"))
                .setHeader(MqttHeaders.QOS, 0)
                .build();
        mqttOutbound.handleMessage(message);
    }

}
