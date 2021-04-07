package com.bugull.hithiumfarmweb.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;

@Configuration
public class MqttConfig {
    @Resource
    PropertiesConfig propertiesConfig;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(propertiesConfig.getMqttUsername());
        mqttConnectOptions.setPassword(propertiesConfig.getMqttPassword().toCharArray());
        mqttConnectOptions.setKeepAliveInterval(20);
        String[] uris = new String[]{propertiesConfig.getMqttServer()};
        mqttConnectOptions.setServerURIs(uris);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setMaxInflight(propertiesConfig.getInflight());
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(propertiesConfig.getMqttClientIdPub(), mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }
}
