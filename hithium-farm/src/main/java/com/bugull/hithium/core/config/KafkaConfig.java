package com.bugull.hithium.core.config;


import com.bugull.hithium.core.util.PropertiesConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(PropertiesConfig.class)
public class KafkaConfig {

    @Resource
    private PropertiesConfig propertiesConfig;

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getServers());
        props.put(ProducerConfig.RETRIES_CONFIG, propertiesConfig.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, propertiesConfig.getBatchSize());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, propertiesConfig.getBufferMemory());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,propertiesConfig.getMaxRequestSize());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,propertiesConfig.getRequestTimeout());
        props.put(ProducerConfig.LINGER_MS_CONFIG,propertiesConfig.getLinger());
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,propertiesConfig.getRetriesBackoff());
        return props;
    }
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }
}
