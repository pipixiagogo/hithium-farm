package com.bugull.hithium.integration.service;

import com.bugull.hithium.core.enumerate.ProjectType;
import com.bugull.hithium.core.util.PropertiesConfig;
import com.bugull.hithium.integration.customThreadFactory.CustomThreadFactory;
import com.bugull.hithium.integration.message.JMessage;
import com.bugull.hithium.integration.trans.MessageTransformer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
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

import static com.bugull.hithium.core.common.Const.TAKE_MAIN_TASK;


@Configuration
@EnableConfigurationProperties(PropertiesConfig.class)
public class MqttService {

    @Resource
    private PropertiesConfig propertiesConfig;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(propertiesConfig.getMqttUsername());
        mqttConnectOptions.setPassword(propertiesConfig.getMqttPassword().toCharArray());
        mqttConnectOptions.setKeepAliveInterval(propertiesConfig.getKeepAliveInterval());
        String[] uris = new String[]{propertiesConfig.getMqttServer()};
        mqttConnectOptions.setServerURIs(uris);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setMaxInflight(propertiesConfig.getInflight());
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
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

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(propertiesConfig.getMqttClientIdPub(), mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }

    IntegrationFlow kcEssDeviceSubFlow() {
        return flow -> flow.handle("kCEssDeviceService", "handle");
    }

    private IntegrationFlow syncTimeSubFlow() {
        return flow -> flow.handle("syncTimeService", "handle");
    }

    IntegrationFlow onofflineSubFlow() {
        return flow -> flow.handle("onOffService", "deviceOnOff");
    }

    IntegrationFlow dataReportSubFlow() {
        return flow -> flow.handle("dateReportService", "handle");
    }

    IntegrationFlow unknowSubFlow() {
        return flow -> flow.handle("unknowService", "handle");
    }

    private IntegrationFlow rdbUpgradeSubFlow() {
        return flow -> flow.handle("rdbUpgradeService", "handle");
    }

    private IntegrationFlow bmuUpgradeSubFlow() {
        return flow -> flow.handle("bmuUpgradeService", "handle");
    }

    private IntegrationFlow restartSubFlow() {
        return flow -> flow.handle("restartService", "handle");
    }

    private IntegrationFlow configSubFlow() {
        return flow -> flow.handle("configService", "handle");
    }

    private IntegrationFlow breakdownSubFlow() {
        return flow -> flow.handle("breakdownService", "handle");
    }

    private IntegrationFlow configFileSubFlow() {
        return flow -> flow.handle("configFileService", "handle");
    }
}
