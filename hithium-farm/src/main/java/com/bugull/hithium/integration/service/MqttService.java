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
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.bugull.hithium.core.common.Const.TAKE_MAIN_TASK;


@Configuration
@EnableConfigurationProperties(PropertiesConfig.class)
@EnableIntegration
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
        mqttConnectOptions.setAutomaticReconnect(propertiesConfig.getMqttAutoReconnect());
        mqttConnectOptions.setMaxInflight(propertiesConfig.getInflight());
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    /**
     * 接收处理分配消息 单线程
     * @return
     */
    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInputChannel())
//                使用多线程获取EMQ推送过来数据 需要进行上一次数据运算的
                .channel(c -> c.executor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1, new CustomThreadFactory(TAKE_MAIN_TASK))))
                //消息处理转换
                .handle(MessageTransformer::mqttMessage2JMessage)
                //路由转换至对应处理扩机
                .<JMessage, ProjectType>route(JMessage::getProjectType,
                        mapping -> mapping
                                .subFlowMapping(ProjectType.KE_ESS, kcEssDeviceSubFlow())
                                .subFlowMapping(ProjectType.UNKNOW, unknowSubFlow())
                                .defaultOutputToParentFlow())
                .get();
    }

    /**
     * 最大到达QOS设置为2  接收到消息的QOS取决于 发送消息的QOS 和 接收消息的QOS 哪个大取哪个
     * @return
     */
    @Bean
    public MessageProducerSupport  mqttInbound() {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        MqttPahoClientFactory mqttClientFactoryInbound = mqttClientFactory();
        mqttClientFactoryInbound.getConnectionOptions().setCleanSession(!propertiesConfig.getMqttCleanSession());
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                propertiesConfig.getMqttClientIdSub(),
                mqttClientFactoryInbound, propertiesConfig.getMqttSubtopics());
        adapter.setCompletionTimeout(propertiesConfig.getCompleteTimeout());
        adapter.setConverter(converter);
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }


    @Bean
    public MessageHandler mqttOutbound() {
        mqttClientFactory().getConnectionOptions().setCleanSession(propertiesConfig.getMqttCleanSession());
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(propertiesConfig.getMqttClientIdPub(), mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }

    //注册对应处理业务逻辑
    IntegrationFlow kcEssDeviceSubFlow() {
        return flow -> flow.handle("kCEssDeviceService", "handle");
    }


    IntegrationFlow unknowSubFlow() {
        return flow -> flow.handle("unknowService", "handle");
    }

}
