package com.mypro.kafka;

import com.mypro.kafka.util.MsgProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author fgzhong
 * @description: 生产者，生产数据到kafka
 * @since 2019/7/13
 */
public class ProducerMain {

    Producer producer;

    public ProducerMain() {
        init();
    }

    public void init() {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", KafkaConstant.SERVER_ADDR);
        // io.confluent.kafka.serializers.KafkaAvroSerializer
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("auto.create.topics.enable", "true");
        producer = new KafkaProducer<String, String>(kafkaProps);
    }

    public void seedMessage(String key, String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConstant.TOPIC, key, msg);
        try{
            producer.send(record);
        } catch(Exception e) {
            e.printStackTrace();//连接错误、No Leader错误都可以通过重试解决；消息太大这类错误kafkaProducer不会进行任何重试，直接抛出异常
        }
    }



}
