package com.akadimia.msg.kafka;

import com.akadimia.msg.ser.ReservationRequestSerializer;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaConfig {

    private Properties properties ;

    private KafkaConfig(){
        properties = new Properties();
    }

    public static KafkaConfig getInstance(String kafkaAddress, String kafkaGroup) {
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.properties.setProperty("bootstrap.servers", kafkaAddress);
        kafkaConfig.properties.setProperty("group.id", kafkaGroup);
        kafkaConfig.properties.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
        kafkaConfig.properties.setProperty("value.serializer", ReservationRequestSerializer.class.getCanonicalName());
        return kafkaConfig;
    }

    public <T> FlinkKafkaConsumer011<T> createConsumerForTopic
            (String topic, DeserializationSchema<T> deserializationSchema) {
        return new FlinkKafkaConsumer011<T>(topic, deserializationSchema, properties);
    }

    public <T> FlinkKafkaProducer011<T> createProducerForTopic(
            String topic, SerializationSchema<T> serializationSchema){
        return new FlinkKafkaProducer011<>(topic, serializationSchema, properties);
    }

    public Properties getProperties() {
        return properties;
    }
}
