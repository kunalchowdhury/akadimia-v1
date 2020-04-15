package com.akadimia.flow.pipeline;

import com.akadimia.dao.ProcessingRequestDao;
import com.akadimia.msg.kafka.KafkaConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public abstract class AbstractPipeline {
    final String name;
    //final KafkaConfig kafkaConfig;

    ProcessingRequestDao processingRequestDao;

    AbstractPipeline(String name, String address, String kafkaConsumerGroup) {
        this.name = name;

      //  this.kafkaConfig = KafkaConfig.getInstance(address, kafkaConsumerGroup);
       // this.processingRequestDao = new ProcessingRequestDao("localhost");

    }

    protected abstract void addSource();

    protected abstract void addSink();

    protected abstract void process();

    public void execute() {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();
        addSource();
        process();
        addSink();
        try {
            environment.execute(name);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
