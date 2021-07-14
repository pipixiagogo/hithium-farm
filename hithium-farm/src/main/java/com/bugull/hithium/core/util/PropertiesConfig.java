package com.bugull.hithium.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConfigurationProperties
public class PropertiesConfig {
    private static final Logger log = LoggerFactory.getLogger(PropertiesConfig.class);
    public static final String MONGO_HOST = "mongo.host";
    public static final String MONGO_PORT = "mongo.port";
    public static final String MONGO_DB = "mongo.database";
    public static final String MONGO_USERNAME = "mongo.username";
    public static final String MONGO_PASSWORD = "mongo.password";



    @Value("${mqtt.server}")
    private String mqttServer;

    @Value("${mqtt.username}")
    private String mqttUsername;

    @Value("${mqtt.password}")
    private String mqttPassword;

    @Value("${mqtt.clientid.subscriber}")
    private String mqttClientIdSub;

    @Value("${mqtt.keepalive.interval}")
    private Integer keepAliveInterval;


    @Value("${mqtt.clientid.publisher}")
    private String mqttClientIdPub;

    @Value("#{'${mqtt.subtopic}'.split(',')}")
    private List<String> mqttSubtopics;

    @Value("${mqtt.complete.timeout}")
    private long completeTimeout;

    @Value("${mqtt.inflight}")
    private int inflight;

    /**
     * kafka配置
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.producer.retries}")
    private int retries;

    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private int bufferMemory;
    @Value("${kafka.send.topic}")
    private String kafkaTopic;
    @Value("${spring.kafka.producer.properties.max.request.size}")
    private int maxRequestSize;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private int requestTimeout;
    @Value("${spring.kafka.producer.properties.linger.ms}")
    private long linger;

    @Value("${spring.kafka.producer.retries.backoff.ms}")
    private long retriesBackoff;

    @Value("${alter.breakdownlog.status.start}")
    private int alterBreakdownlogStatusStart;
    @Value("${alter.breakdownlog.status.end}")
    private int alterBreakdownlogStatusEnd;
    @Value("${redis.data.cache.switch}")
    private boolean redisCacheSwitch;
    @Value("${send.to.kafka}")
    private boolean sendToKafka;

    public boolean isSendToKafka() {
        return sendToKafka;
    }

    public boolean isRedisCacheSwitch() {
        return redisCacheSwitch;
    }

    public int getAlterBreakdownlogStatusStart() {
        return alterBreakdownlogStatusStart;
    }

    public int getAlterBreakdownlogStatusEnd() {
        return alterBreakdownlogStatusEnd;
    }

    public int getInflight() {
        return inflight;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public long getCompleteTimeout() {
        return completeTimeout;
    }


    public String getMqttServer() {
        return mqttServer;
    }

    public String getMqttUsername() {
        return mqttUsername;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public String getMqttClientIdSub() {
        return mqttClientIdSub;
    }

    public String getMqttClientIdPub() {
        return mqttClientIdPub;
    }

    public String[] getMqttSubtopics() {
        String[] topics = new String[mqttSubtopics.size()];
        topics = mqttSubtopics.toArray(topics);
        return topics;
    }

    public String getServers() {
        return servers;
    }

    public int getRetries() {
        return retries;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getBufferMemory() {
        return bufferMemory;
    }


    public int getMaxRequestSize() {
        return maxRequestSize;
    }


    public int getRequestTimeout() {
        return requestTimeout;
    }

    public long getLinger() {
        return linger;
    }

    public long getRetriesBackoff() {
        return retriesBackoff;
    }


}
