package com.bugull.hithium.integration.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaConsumerService {

    //当监听不同分区时候 两个都能监听到  containerFactory:使用自定义配置
    @KafkaListener(groupId = "test-consumer", topics = "test-topic",containerFactory = "kafkaListenerContainerFactory",topicPartitions = {
            @TopicPartition(topic = "test-topic", partitions = {"0"})
    })
    public void receivePartitionOfFirst(List<ConsumerRecord<String, String>> cr, Acknowledgment ack) {
//        System.out.println(cr.value() + "--------" + cr.key() + "--------" + cr);
        //不提交offer查看再次启动是否会有消息推送过来  不提交下次启动消息还会发送过来没签收货物
        for(int i=0;i<cr.size();i++){
            System.out.println(cr.get(i).value() + "--------" + cr.get(i).key() + "--------" + cr);
            ack.acknowledge();//手动提交 直接提交Offset
        }
    }

    //当监听不同分区时候 两个都能监听到  containerFactory:使用自定义配置
    @KafkaListener(groupId = "test-consumer", topics = "test-topic",containerFactory = "kafkaListenerContainerFactory",topicPartitions = {
            @TopicPartition(topic = "test-topic", partitions = {"1"})
    })
    public void receivePartitionOfSecond(List<ConsumerRecord<String, String>> cr, Acknowledgment ack) {
//        System.out.println(cr.value() + "--------" + cr.key() + "--------" + cr);
        //不提交offer查看再次启动是否会有消息推送过来  不提交下次启动消息还会发送过来没签收货物
        for(int i=0;i<cr.size();i++){
            System.out.println(cr.get(i).value() + "--------" + cr.get(i).key() + "--------" + cr);
            ack.acknowledge();//手动提交 直接提交Offset
        }

    }

    //当监听不同分区时候 两个都能监听到  containerFactory:使用自定义配置
    @KafkaListener(groupId = "test-consumer", topics = "test-topic",containerFactory = "kafkaListenerContainerFactory",topicPartitions = {
            @TopicPartition(topic = "test-topic", partitions = {"2"})
    })
    public void receivePartitionOfThree(List<ConsumerRecord<String, String>> cr, Acknowledgment ack) {
//        System.out.println(cr.value() + "--------" + cr.key() + "--------" + cr);
        //不提交offer查看再次启动是否会有消息推送过来  不提交下次启动消息还会发送过来没签收货物
        for(int i=0;i<cr.size();i++){
            System.out.println(cr.get(i).value() + "--------" + cr.get(i).key() + "--------" + cr);
            ack.acknowledge();//手动提交 直接提交Offset
        }

    }
}
