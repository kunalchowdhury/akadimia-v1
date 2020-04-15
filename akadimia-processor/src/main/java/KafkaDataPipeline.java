import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import java.util.Properties;

public class KafkaDataPipeline {
    public static FlinkKafkaConsumer011<String> createStringConsumerForTopic
            (String topic, String kafkaAddress, String kafkaGroup ) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id",kafkaGroup);
        FlinkKafkaConsumer011<String> consumer = new FlinkKafkaConsumer011<String>(topic, new SimpleStringSchema(), props);
        return consumer;
    }

    public static FlinkKafkaProducer011<String> createStringProducer(
            String topic, String kafkaAddress){

        return new FlinkKafkaProducer011<>(kafkaAddress,
                topic, new SimpleStringSchema());
    }

    public static class WordsCapitalizer implements MapFunction<String, String> {
        @Override
        public String map(String s) {
            return s.toUpperCase();
        }
    }

    public static void main(String[] args) throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "baeldung";
        String address = "localhost:9092";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();
        FlinkKafkaConsumer011<String> flinkKafkaConsumer = createStringConsumerForTopic(
                inputTopic, address, consumerGroup);
        DataStream<String> stringInputStream = environment
                .addSource(flinkKafkaConsumer);

        FlinkKafkaProducer011<String> flinkKafkaProducer = createStringProducer(
                outputTopic, address);

        stringInputStream
                .map(new WordsCapitalizer())
                .addSink(flinkKafkaProducer);
        environment.execute("Test Kafka");
    }
}
