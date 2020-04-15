package com.akadimia.flow.pipeline;

import com.akadimia.dao.ProcessingRequestDao;
import com.akadimia.entity.user.ReservationRequest;
import com.akadimia.flow.stream.parsers.RRInstFilterJoinProcessFunction;
import com.akadimia.flow.stream.parsers.ReservedTutorStreamParser;
import com.akadimia.msg.ser.ReservationRequestDeserializer;
import com.akadimia.msg.ser.ReservationRequestSerializer;
import com.akadimia.util.WorkflowUtil;
import jodd.introspector.MapperFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.RichAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.redisson.Redisson;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationRequestPipeline  {
    private final String subject;
    private final String outputTopic;
    private final String grp;
    private final String filteredSessionsTopic;
    private final String kafkaAddress;

    public ReservationRequestPipeline(String kafkaAddress,
                                         String kafkaGrp,
                                         String subject) {
       // super("Reservation Request Pipeline", kafkaAddress, kafkaGrp);
        this.subject = "registration_"+subject; // subject = life_science
        this.outputTopic = "registration_result_"+subject;
        this.filteredSessionsTopic = "filtered_sessions_"+subject;
        this.grp = kafkaGrp;
        this.kafkaAddress = kafkaAddress;
    }


    protected void addSource() {}
    protected void addSink() {}

    static class CustomRichWindowFunction extends RichAllWindowFunction<ReservationRequest, String, TimeWindow>{

            private transient RedissonClient redisson;
            private transient RReadWriteLock lock;
            private transient KafkaProducer<String, String> loopbackProducer;
            private transient ThreadLocal<SimpleDateFormat> dateFormatThreadLocal;
            private transient Map<String, Boolean> map ;
            private transient Map<String, Boolean> rejectionMap ;

            private String mainTopic;
            private String filteredTopic;
            private String outputTopic;

        public CustomRichWindowFunction(String mainTopic, String filteredTopic, String outputTopic) {
            this.mainTopic = mainTopic;
            this.filteredTopic = filteredTopic;
            this.outputTopic = outputTopic;
        }

        @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);

                Config config = new Config();
                config.useSingleServer()
                        .setAddress("redis://127.0.0.1:6379");
                redisson = Redisson.create(config);
                // operations with Redis based Lock
                // implements java.util.concurrent.locks.Lock
                lock = redisson.getReadWriteLock("lock");

                Properties props = new Properties();
                props.setProperty("bootstrap.servers", "localhost:9092" );
                props.setProperty("group.id","akadimia");
                props.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
                props.setProperty("value.serializer", StringSerializer.class.getCanonicalName());
                loopbackProducer = new KafkaProducer<String, String>(props);
                dateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ"));
                map = new ConcurrentHashMap<>();

                rejectionMap = new ConcurrentHashMap<>();
            }

            private String generateUniqueUrl() {
                return UUID.randomUUID().toString();
            }

            @Override
            public void apply(TimeWindow timeWindow, Iterable<ReservationRequest> reservationRequests, Collector<String> collector) throws Exception {

                reservationRequests.forEach(reservationRequest -> {
                    if (reservationRequest.getShouldProcess() == null || reservationRequest.getShouldProcess()) {
                        String s = "https://"+System.getProperty("host.name")+":4200/meeting/"+generateUniqueUrl();
                        reservationRequest.setClassLink(s);
                        Tuple2<Optional<Map<String, String>>, ProcessingRequestDao.Status> saveReservationRequest =
                                ProcessingRequestDao.saveReservationRequest(redisson, lock, reservationRequest);
                        if (saveReservationRequest.f1 == ProcessingRequestDao.Status.FAILED) {
                            // loopback to this queue again.
                            loopbackProducer.send(new ProducerRecord<>(filteredTopic,
                                    reservationRequest.getId() + "=false"));

                            rejectionMap.computeIfAbsent(reservationRequest.getUserEmailId() + "-" + reservationRequest.getId(),
                                    (MapperFunction<String, Boolean>) s1 -> {
                                        try {
                                            WorkflowUtil.sendMail(reservationRequest.getUserEmailId(), "User Reservation Confirmation",
                                                    "Your request is REJECTED for the class for " +
                                                            reservationRequest.getInstructorName() + " , subject - " +
                                                            reservationRequest.getSubject() + " from : " +
                                                            reservationRequest.getStartTime()
                                                            + " to " + reservationRequest.getEndTime() + "." +
                                                            " as the slot has already been taken .You can search for a new slot. ");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        loopbackProducer.send(new ProducerRecord<>(outputTopic,
                                                reservationRequest.getUserEmailId() + "," + reservationRequest.getInstructorName() + ",1," + reservationRequest.getSubject()
                                                        + "," + reservationRequest.getStartTime() + "," + reservationRequest.getEndTime() + "," + "" + ", REJECTED"));
                                        return true;
                                    }
                            );
                        } else {
                            try {
                                map.computeIfAbsent(reservationRequest.getUserEmailId() + "-" + reservationRequest.getId(), s1-> {

                                        loopbackProducer.send(new ProducerRecord<>(outputTopic,
                                                reservationRequest.getUserEmailId() + "," + reservationRequest.getInstructorName() + ",<1>," + reservationRequest.getSubject()
                                                        + "," + reservationRequest.getStartTime() + "," + reservationRequest.getEndTime() + "," + reservationRequest.getClassLink() + ", ACCEPTED"));

                                loopbackProducer.send(new ProducerRecord<>(outputTopic,
                                        reservationRequest.getInstructorEmailId() + "," + reservationRequest.getInstructorName() + ",<1>," + reservationRequest.getSubject()
                                                + "," + reservationRequest.getStartTime() + "," + reservationRequest.getEndTime() + "," + reservationRequest.getClassLink() + ", ACCEPTED"));

                                return true;

                                });



                                WorkflowUtil.sendMail(reservationRequest.getUserEmailId(), "Akadimia User Reservation Confirmation",
                                        "Your request is CONFIRMED for class for " +
                                                reservationRequest.getInstructorName() + " , subject - " +
                                                reservationRequest.getSubject() + " from : " +
                                                reservationRequest.getStartTime()
                                                + " to " + reservationRequest.getEndTime() + "." +
                                                " For attending pls connect to   " + s);


                                WorkflowUtil.sendMail(reservationRequest.getInstructorEmailId(), "Akadimia Instructor Reservation Confirmation",
                                        "Your class is CONFIRMED for class for " +
                                                reservationRequest.getInstructorName() + " , subject - " +
                                                reservationRequest.getSubject() + " from " +
                                                reservationRequest.getStartTime()
                                                + " to " + reservationRequest.getEndTime() + "." +
                                                " Link to Online class is " + s);

                                loopbackProducer.send(new ProducerRecord<>(filteredTopic,
                                        reservationRequest.getId() + "=false"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        collector.collect(WorkflowUtil.getReplyMsg(reservationRequest.getId(),
                                saveReservationRequest.f1.name()));


                    } else {

                        try {
                            rejectionMap.computeIfAbsent(reservationRequest.getUserEmailId() + "-" + reservationRequest.getId(),
                                    (MapperFunction<String, Boolean>) s -> {
                                        try {
                                            WorkflowUtil.sendMail(reservationRequest.getUserEmailId(), "User Reservation Confirmation",
                                                    "Your request is REJECTED for the class for " +
                                                            reservationRequest.getInstructorName() + " , subject - " +
                                                            reservationRequest.getSubject() + " from : " +
                                                            reservationRequest.getStartTime()
                                                            + " to " + reservationRequest.getEndTime() + "." +
                                                            " as the slot has already been taken .You can search for a new slot. ");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if(map.containsKey(reservationRequest.getUserEmailId() + "-" + reservationRequest.getId())) {
                                            loopbackProducer.send(new ProducerRecord<>(outputTopic,
                                                    reservationRequest.getUserEmailId() + "," + reservationRequest.getInstructorName() + ",1," + reservationRequest.getSubject()
                                                            + "," + reservationRequest.getStartTime() + "," + reservationRequest.getEndTime() + "," + "" + ", REJECTED"));
                                        }
                                        return true;
                                    }
                            );

                        } catch (Exception e) {

                            e.printStackTrace();
                        }



                        collector.collect(WorkflowUtil.getReplyMsg(reservationRequest.getId(),
                                ProcessingRequestDao.Status.FAILED.name()));
                    }

                });
    }}


    public void process() {
/*
        FlinkKafkaConsumer011<ReservationRequest> flinkReservationRequestConsumer =
                kafkaConfig.createConsumerForTopic(subject, new ReservationRequestDeserializer());
*/


        Properties props = new Properties();
        props.setProperty("bootstrap.servers", this.kafkaAddress );
        props.setProperty("group.id",grp);
        props.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
        props.setProperty("value.serializer", ReservationRequestSerializer.class.getCanonicalName());

        FlinkKafkaConsumer011<ReservationRequest> flinkReservationRequestConsumer = new FlinkKafkaConsumer011<ReservationRequest>(subject, new ReservationRequestDeserializer(), props);
        flinkReservationRequestConsumer.setStartFromEarliest();

        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();

        environment.enableCheckpointing(2000);// start a checkpoint every 2seconds
        CheckpointConfig config = environment.getCheckpointConfig();
        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);// set mode to exactly-once (this is the default)
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        DataStream<ReservationRequest> reservationRequestDataStream = environment
                .addSource(flinkReservationRequestConsumer).returns(ReservationRequest.class)
                .setParallelism(1)
                .keyBy("id");

        props = new Properties();
        props.setProperty("bootstrap.servers", this.kafkaAddress );
        props.setProperty("group.id",grp);
        props.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
        props.setProperty("value.serializer", StringSerializer.class.getCanonicalName());

        FlinkKafkaConsumer011<String> filteredInstructors  = new FlinkKafkaConsumer011<String>(filteredSessionsTopic, new SimpleStringSchema(), props);
        filteredInstructors.setStartFromEarliest();

        KeyedStream<Tuple2<String, Boolean>, Tuple> filteredInstructorsStream = environment
                .addSource(filteredInstructors)
                .setParallelism(1)
                .flatMap(new ReservedTutorStreamParser()).name("Filter FlatMap")
                .keyBy(0);


        FlinkKafkaProducer011<String> producer = new FlinkKafkaProducer011<String>(outputTopic, new SimpleStringSchema(), props);

        //ProcessingRequestDao processingRequestDao = new ProcessingRequestDao();
        SingleOutputStreamOperator<String> reservationResultStream = reservationRequestDataStream.connect(filteredInstructorsStream)
                .process(new RRInstFilterJoinProcessFunction()).setParallelism(1).uid("join-1").name("Co-JoinProcess")
                .windowAll(SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(3)))
                .apply(new CustomRichWindowFunction(subject, filteredSessionsTopic, outputTopic));


        //kafkaConfig.createProducerForTopic(outputTopic, new SimpleStringSchema());
      //  reservationResultStream.addSink(producer);

        try {
            environment.execute("Task");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String generateUniqueUrl() {
        return UUID.randomUUID().toString();
    }
}
