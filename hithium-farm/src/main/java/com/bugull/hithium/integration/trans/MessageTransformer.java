package com.bugull.hithium.integration.trans;

import com.alibaba.fastjson.JSONObject;
import com.bugull.hithium.core.common.Const;
import com.bugull.hithium.core.enumerate.DataType;
import com.bugull.hithium.core.enumerate.ProjectType;
import com.bugull.hithium.core.util.ByteUtil;
import com.bugull.hithium.integration.message.JMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MessageTransformer {
    private static final String DISCONNECT = "disconnected";
    private static final String CONNECT = "connected";
    private static final short MAGIC_NUM = ByteUtil.toShort(new byte[]{(byte) 0xbb, (byte) 0xcd});
    private static final short MIN_DATA_LEN = 11;
    private static final Logger log = LoggerFactory.getLogger(MessageTransformer.class);

    public static JMessage mqttMessage2JMessage(byte[] payload, Map<String, Object> headers) {
        try {
            //上下线日志也会受到  不处理 或者到时候处理下 存下数据库
            String topic = headers.get(MqttHeaders.RECEIVED_TOPIC).toString();
            if (topic.startsWith("$ESS")) {
                return doEssMsg(payload, headers, topic);
            } else {
                JMessage jMessage = new JMessage();
                jMessage.setTopic(topic);
                jMessage.setProjectType(ProjectType.UNKNOW);
                return jMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据解析错误:{}", e);
            log.info(e.getMessage());
            JMessage jMessage = new JMessage();
            jMessage.setProjectType(ProjectType.UNKNOW);
            return jMessage;
        }
    }
    private static JMessage doEssMsg(byte[] payload, Map<String, Object> headers, String topic) throws UnsupportedEncodingException {
        String payLoadMsg = new String(payload, "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(payLoadMsg);
        String projectType = (String) jsonObject.get(Const.PROJECT_TYPE);
        String dataType = (String) jsonObject.get(Const.DATA_TYPE);
        String[] topicSlices = topic.split( JMessage.TOPIC_SPLIT );
        String deviceName = topicSlices[1];
        JMessage jMessage = new JMessage();
        jMessage.setTopic(topic);
        jMessage.setDeviceName(deviceName);
        jMessage.setProjectType(ProjectType.value(projectType));
        jMessage.setDataType(DataType.value(dataType));
        jMessage.setPayloadMsg(payLoadMsg);
        return jMessage;
    }

}
