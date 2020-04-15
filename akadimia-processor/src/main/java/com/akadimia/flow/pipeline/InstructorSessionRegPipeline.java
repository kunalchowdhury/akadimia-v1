package com.akadimia.flow.pipeline;

import com.akadimia.dao.ProcessingRequestDao;
import com.akadimia.entity.user.InstructorSessionRegistration;
import com.akadimia.msg.ser.InstructorSessionDeserializer;
import com.akadimia.util.WorkflowUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import javax.annotation.Nullable;
import java.util.Date;

public class InstructorSessionRegPipeline extends AbstractPipeline {

    private final String subject;
    private final String outputTopic;
    private DataStreamSource<InstructorSessionRegistration> instructorSessionDataStreamSource;
    private SingleOutputStreamOperator<String> instructorSessionResultStream;

    public static class ISTimestampAssigner
            implements AssignerWithPunctuatedWatermarks<InstructorSessionRegistration> {


        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(InstructorSessionRegistration instructorSessionRegistration, long l) {
            return new Watermark(l - 1500);
        }

        @Override
        public long extractTimestamp(InstructorSessionRegistration instructorSessionRegistration, long l) {
            return instructorSessionRegistration.getSentAt();
        }
    }



    InstructorSessionRegPipeline(String subject, String kafkaAddress, String kafkaGrp) {
        super("Instructor Session Pipeline", kafkaAddress, kafkaGrp);
        this.subject = "instructor_session_"+subject; //subject = life_science
        this.outputTopic = "instructor_session_result_"+subject;
    }

    @Override
    protected void addSource() {
       /* FlinkKafkaConsumer011<InstructorSessionRegistration> flinkConsumer =
                kafkaConfig.createConsumerForTopic(subject, new InstructorSessionDeserializer());
        flinkConsumer.setStartFromEarliest();
        flinkConsumer.assignTimestampsAndWatermarks(new ISTimestampAssigner());
        instructorSessionDataStreamSource = environment
                .addSource(flinkConsumer);*/


    }

    @Override
    protected void addSink() {
        //FlinkKafkaProducer011<String> producer = kafkaConfig.createProducerForTopic(outputTopic, new SimpleStringSchema());
        //instructorSessionResultStream.addSink(producer);
    }

    @Override
    protected void process() {
        /*instructorSessionResultStream = instructorSessionDataStreamSource.windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(1)))
                .apply((AllWindowFunction<InstructorSessionRegistration, String, TimeWindow>) (timeWindow,
                                                                                               iterable, collector) -> {
                    for (InstructorSessionRegistration instructorSessionRegistration : iterable) {
                        Tuple2<String, ProcessingRequestDao.Status> statusTuple2 =
                                processingRequestDao.saveInstructorSession(instructorSessionRegistration);
                        collector.collect(WorkflowUtil.getReplyMsg(statusTuple2.f0, statusTuple2.f1.name()));
                        if (statusTuple2.f1 != ProcessingRequestDao.Status.FAILED) {
                            WorkflowUtil.sendMail(instructorSessionRegistration.getEmailId(),
                                    "Instructor Registration Confirmation",
                                    "Your registration as an instructor for "+ instructorSessionRegistration.getSubject()+
                                    " from "+ dateFormatThreadLocal.get().format(new Date(instructorSessionRegistration.getSessionStartTime()))+
                                    " to "+ dateFormatThreadLocal.get().format(new Date(instructorSessionRegistration.getSessionEndTime()))
                            + " has been accepted. Please be available at this time to take up your class. Regards - Team Akadimia ");
                        }

                    }

                });*/
    }
}
