package com.bugull.hithium.core.config;

import com.bugull.hithium.core.util.PropertiesConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode.MANUAL;

@Component
public class KafkaConsumerConfig {
    @Resource
    private PropertiesConfig propertiesConfig;

    public Map<String, Object> constomerConfigs() {
        Map<String, Object> props = new HashMap<>();
        //消费组ID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
        //bootstrap.servers 192.168.32.100:9093
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getServers());
        //是否开启自动提交配置
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // 消费者补偿自动发送给 Kafka 的频率 如果开启了自动提交生效
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //连接会话超时时间
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //一次poll最多返回的记录数 不代表一定到这个值才返回 有可能返回一条
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        //key.serializer  可序列化
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        //value.serializer  可序列化
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(constomerConfigs()));
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(3);
        container.setBatchListener(true);
        //因为自动提交是在kafka拉取到数据之后就直接提交，这样很容易丢失数据
        // 配置手动提交offset 自动提交有可能出现重复消费
        container.getContainerProperties().setAckMode(MANUAL);
        return container;
    }
}
