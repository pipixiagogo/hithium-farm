package com.bugull.hithiumfarmweb.config;

import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@EnableConfigurationProperties
public class PropertiesConfig {

    public static final String MONGO_HOST = "mongo.host";
    public static final String MONGO_PORT = "mongo.port";

    public static final String MONGO_DB = "mongo.database";
    public static final String MONGO_USERNAME = "mongo.username";
    public static final String MONGO_PASSWORD = "mongo.password";

    @Value("${mqtt.clientid.subscriber}")
    private String mqttClientIdSub;

    @Value("#{'${mqtt.subtopic}'.split(',')}")
    private List<String> mqttSubtopics;

    @Value("${mqtt.complete.timeout}")
    private int completeTimeout;

    @Value("${token.expire.time}")
    private int tokenExpireTime;

    @Value("${mqtt.username}")
    private String mqttUsername;

    @Value("${mqtt.password}")
    private String mqttPassword;

    @Value("${mqtt.server}")
    private String mqttServer;

    @Value("${mqtt.inflight}")
    private Integer inflight;

    @Value("${mqtt.clientid.publisher}")
    private String mqttClientIdPub;

    public String getMqttClientIdSub() {
        return mqttClientIdSub;
    }

    public String[] getMqttSubtopics() {
        String[] topics = new String[mqttSubtopics.size()];
        topics = mqttSubtopics.toArray(topics);
        return topics;
    }

    public Integer getCompleteTimeout() {
        return completeTimeout;
    }

    public int getTokenExpireTime() {
        return tokenExpireTime;
    }

    public String getMqttUsername() {
        return mqttUsername;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public String getMqttServer() {
        return mqttServer;
    }

    public Integer getInflight() {
        return inflight;
    }

    public String getMqttClientIdPub() {
        return mqttClientIdPub;
    }
}
