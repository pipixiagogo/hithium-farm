package com.bugull.hithiumfarmweb.config;

import com.bugull.hithiumfarmweb.enumerate.ProjectType;
import com.bugull.hithiumfarmweb.message.JMessage;
import com.bugull.hithiumfarmweb.trans.MessageTransformer;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

import static com.bugull.hithiumfarmweb.common.Const.TAKE_MAIN_TASK;

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
        String [] uris = new String[]{propertiesConfig.getMqttServer()};
        mqttConnectOptions.setServerURIs(uris);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setMaxInflight(propertiesConfig.getInflight());
        DefaultMqttPahoClientFactory factory=new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInbound())
                .channel(c -> c.executor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1, new CustomThreadFactory(TAKE_MAIN_TASK))))
                .handle(MessageTransformer::mqttMessage2JMessage)
                .<JMessage, ProjectType>route(JMessage::getProjectType,
                        mapping -> mapping
                                .subFlowMapping(ProjectType.KE_ESS, kcEssDeviceSubFlow())
                                .subFlowMapping(ProjectType.UNKNOW, unknowSubFlow())
                                .defaultOutputToParentFlow()
                )
                .get();
    }
    @Bean
    public MessageProducerSupport mqttInbound() {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                propertiesConfig.getMqttClientIdSub(),
                mqttClientFactory(), propertiesConfig.getMqttSubtopics());
        adapter.setCompletionTimeout(propertiesConfig.getCompleteTimeout());
        adapter.setConverter(converter);
        adapter.setQos(0);
        return adapter;
    }

    IntegrationFlow kcEssDeviceSubFlow() {
        return flow -> flow.handle("kCEssDeviceService", "handle");
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(propertiesConfig.getMqttClientIdPub(), mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }
    IntegrationFlow unknowSubFlow() {
        return flow -> flow.handle("unknowService", "handle");
    }
}
