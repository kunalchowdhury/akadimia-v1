import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.util.Collector;

public class CoProcessTest{

    public static class WordStreamParser implements FlatMapFunction<String,Tuple2<String,Integer>> {
        @Override
        public void flatMap(String s, Collector<Tuple2<String,Integer>> collector) throws Exception{
            for(String word : s.trim().split(" ")) {
                collector.collect(Tuple2.of(word, 100));
            }
        }
    }

    public static class FilterStreamParser implements FlatMapFunction<String,Tuple2<String,Boolean>>{
        @Override
        public void flatMap(String s, Collector<Tuple2<String,Boolean>> collector) throws Exception{
            String[] strVals = s.split("=");
            if(strVals.length == 2){
                String key = strVals[0];
                Boolean val = strVals[1].equals("1") || strVals[1].equals("true");
                collector.collect(Tuple2.of(key, val));
            }
        }
    }

    public static class FilterState{
        public FilterState(){

        }
        public FilterState(Boolean active){
            this.Active = active;
        }
        public Boolean Active;
    }

    public static class JoinProcessFunction extends CoProcessFunction<Tuple2<String,Integer>,Tuple2<String,Boolean>,Tuple2<String,Integer>> {
        private ValueState<FilterState> state;

        @Override
        public void open(Configuration parameters) throws Exception{
            ValueStateDescriptor<FilterState> desc = new ValueStateDescriptor<>(
                    "filterState",
                    FilterState.class, new FilterState(true)
            );
            state = getRuntimeContext().getState(desc);
        }

        @Override
        public void processElement1(Tuple2<String,Integer> input, Context context, Collector<Tuple2<String,Integer>> collector) throws Exception{
            String key = input.f0;
            FilterState current = state.value();
            if(current.Active) collector.collect(input);//filtering place
        }

        @Override
        public void processElement2(Tuple2<String,Boolean> input, Context context, Collector<Tuple2<String,Integer>> collector) throws Exception{
            FilterState current = state.value();
            current.Active = input.f1;//set state value
            state.update(current);
        }
    }
    public static void main(String[] args) throws Exception{

       // final ParameterTool params = ParameterTool.fromArgs(args);
       // int portWordStream = params.getInt("portStream");
       // int portFilterStream = params.getInt("portFilterStream");
       // int portSink = params.getInt("portSink");
        String stateDir = "/Users/kunal/temp/flink-state-backend" ;//params.get("stateDir");
       // String socketHost = params.get("host");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setStateBackend(new MemoryStateBackend());
        env.enableCheckpointing(2000);// start a checkpoint every 2seconds
        CheckpointConfig config = env.getCheckpointConfig();
        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);// set mode to exactly-once (this is the default)
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        String inputTopicWord = "flink_input_word";
        String inputTopicFilter = "flink_input_filter";
        String outputTopic = "flink_output";
        String consumerGroup = "baeldung";
        String address = "localhost:9092";
        FlinkKafkaProducer011<String> flinkKafkaProducer = KafkaDataPipeline.createStringProducer(
                outputTopic, address);

        FlinkKafkaConsumer011<String> flinkKafkaConsumerWord = KafkaDataPipeline.createStringConsumerForTopic(
                inputTopicWord, address, consumerGroup);

        FlinkKafkaConsumer011<String> flinkKafkaConsumerFilter = KafkaDataPipeline.createStringConsumerForTopic(
                inputTopicFilter, address, consumerGroup);


        DataStream<Tuple2<String,Integer>> wordStream = env
                //.socketTextStream(socketHost, portWordStream).name("Word Stream")
                .addSource(flinkKafkaConsumerWord)
                .setParallelism(1)
                .flatMap(new WordStreamParser()).name("Word FlatMap")
                .keyBy(0);

        DataStream<Tuple2<String,Boolean>> filterStream = env
                //.socketTextStream(socketHost, portFilterStream).name("Filter Stream")
                .addSource(flinkKafkaConsumerFilter)
                .setParallelism(1)
                .flatMap(new FilterStreamParser()).name("Filter FlatMap")
                .keyBy(0);

        wordStream
                .connect(filterStream)
                .process(new JoinProcessFunction()).setParallelism(1).uid("join-1").name("Co-JoinProcess")
                .keyBy(0)
                .window(SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(3)))
                .sum(1).name("Summarize")
                .setParallelism(1)
        .map(new MapFunction<Tuple2<String, Integer>, String>() {
            @Override
            public String map(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.f0 + " , "+ stringIntegerTuple2.f1;
            }
        }).addSink(flinkKafkaProducer);

        /*joinedStream.map((MapFunction<Tuple2<String,Integer>,String>) input -> String.format("%tT | %s Count: %d\n", LocalDateTime.now(), input.f0, input.f1))
                .returns(String.class)
                .writeToSocket(socketHost, portSink, new SimpleStringSchema())
                .setParallelism(1)
                .name("Socket Output");
*/

// execute program
        env.execute("Flink Streaming Stateful Java Example");
    }
}