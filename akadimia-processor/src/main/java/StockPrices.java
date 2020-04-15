/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.flink.api.common.accumulators.AverageAccumulator;
import org.apache.flink.api.common.accumulators.ListAccumulator;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.DeltaTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This example showcases a moderately complex Flink Streaming pipeline.
 * It to computes statistics on stock market data that arrive continuously,
 * and combines the stock market data with tweet streams.
 * For a detailed explanation of the job, check out the blog post unrolling it.
 * To run the example make sure that the service providing the text data
 * is already up and running.
 *
 * <p>
 * To start an example socket text stream on your local machine run netcat from
 * a command line: <code>nc -lk 9999</code>, where the parameter specifies the
 * port number.
 *
 *
 * <p>
 * Usage:
 * <code>StockPrices &lt;hostname&gt; &lt;port&gt; &lt;result path&gt;</code>
 * <br>
 *
 * <p>
 * This example shows how to:
 * <ul>
 * <li>union and join data streams,
 * <li>use different windowing policies,
 * <li>define windowing aggregations.
 * </ul>
 *
 * @see <a href="www.openbsd.org/cgi-bin/man.cgi?query=nc">netcat</a>
 * @see <a href="http://flink.apache.org/news/2015/02/09/streaming-example.html">blogpost</a>
 */
public class StockPrices {

    private static final ArrayList<String> SYMBOLS = new ArrayList<String>(Arrays.asList("SPX", "FTSE", "DJI", "DJT", "BUX", "DAX", "GOOG"));
    private static final Double DEFAULT_PRICE = 1000.;
    private static final StockPrice DEFAULT_STOCK_PRICE = new StockPrice("", DEFAULT_PRICE);

    // *************************************************************************
    // PROGRAM
    // *************************************************************************

    @SuppressWarnings({ "serial", "unused" })
    public static void main(String[] args) throws Exception {

        if (!parseParameters(args)) {
            return;
        }

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //Step 1
        //Read a stream of stock prices from different sources and union it into one stream

        //Read from a socket stream at map it to StockPrice objects
        DataStream<StockPrice> socketStockStream = env.socketTextStream(hostName, port)
                .map(new MapFunction<String, StockPrice>() {
                    private String[] tokens;

                    @Override
                    public StockPrice map(String value) throws Exception {
                        tokens = value.split(",");
                        return new StockPrice(tokens[0], Double.parseDouble(tokens[1]));
                    }
                });

        //Generate other stock streams
        DataStream<StockPrice> SPX_stream = env.addSource(new StockSource("SPX", 10));
        DataStream<StockPrice> FTSE_stream = env.addSource(new StockSource("FTSE", 20));
        DataStream<StockPrice> DJI_stream = env.addSource(new StockSource("DJI", 30));
        DataStream<StockPrice> BUX_stream = env.addSource(new StockSource("BUX", 40));


        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "baeldung";
        String address = "localhost:9092";
        FlinkKafkaProducer011<String> flinkKafkaProducer = KafkaDataPipeline.createStringProducer(
                outputTopic, address);

        //Merge all stock streams together
        @SuppressWarnings("unchecked")
        DataStream<StockPrice> stockStream = socketStockStream.union(SPX_stream, FTSE_stream, DJI_stream, BUX_stream);

        //Step 2
        //Compute some simple statistics on a rolling window
        AllWindowedStream<StockPrice, TimeWindow> windowedStream = stockStream
                .windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)));


        DataStream<StockPrice> lowest = windowedStream.minBy("price");
        DataStream<StockPrice> maxByStock = stockStream.keyBy("symbol").windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .maxBy("price");
        DataStreamSink<String> rollingMean = stockStream.keyBy("symbol").windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .aggregate(new AggregateFunction<StockPrice, AverageAccumulator, StockPrice>() {
                    String symbol ;
                    @Override
                    public AverageAccumulator createAccumulator() {
                        return new AverageAccumulator();
                    }

                    @Override
                    public AverageAccumulator add(StockPrice stockPrice, AverageAccumulator averageAccumulator) {
                        averageAccumulator.add(stockPrice.price);
                        symbol = stockPrice.symbol;
                        return averageAccumulator;
                    }

                    @Override
                    public StockPrice getResult(AverageAccumulator averageAccumulator) {
                        return new StockPrice(symbol, averageAccumulator.getLocalValue());
                    }

                    @Override
                    public AverageAccumulator merge(AverageAccumulator averageAccumulator, AverageAccumulator acc1) {
                        return acc1;
                    }
                }).map(new MapFunction<StockPrice, String>() {
                    @Override
                    public String map(StockPrice stockPrice) throws Exception {
                        return stockPrice.symbol+"  <-> " + stockPrice.price;
                    }
                }).addSink(flinkKafkaProducer);

        //Step 3
        //Use  delta policy to create price change warnings, and also count the number of warning every half minute

        DataStream<String> priceWarnings = stockStream.keyBy("symbol").windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .trigger(DeltaTrigger.of(0.05, new DeltaFunction<StockPrice>() {
                    @Override
                    public double getDelta(StockPrice oldDataPoint, StockPrice newDataPoint) {
                        return Math.abs(oldDataPoint.price - newDataPoint.price);
                    }
                }, stockStream.getType().createSerializer(env.getConfig())))
                .apply(new AllWindowFunction<StockPrice, String, TimeWindow>() {
                    @Override
                    public void apply(TimeWindow timeWindow, Iterable<StockPrice> values, Collector<String> out) throws Exception {
                        if (values.iterator().hasNext()) {
                            out.collect(values.iterator().next().symbol);
                        }
                    }
                });


        DataStream<Count> warningsPerStock = priceWarnings.map(new MapFunction<String, Count>() {
            @Override
            public Count map(String value) throws Exception {
                return new Count(value, 1);
            }
        }).keyBy("symbol").windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .sum("count");

        //Step 4
        //Read a stream of tweets and extract the stock symbols
        DataStream<String> tweetStream = env.addSource(new TweetSource());

        DataStream<String> mentionedSymbols = tweetStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] words = value.split(" ");
                for (String word : words) {
                    out.collect(word.toUpperCase());
                }
            }
        }).filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return SYMBOLS.contains(value);
            }
        });

        DataStream<Count> tweetsPerStock = mentionedSymbols.map(new MapFunction<String, Count>() {
            @Override
            public Count map(String value) throws Exception {
                return new Count(value, 1);
            }
        }).keyBy("symbol")
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(30)))
                .sum("count");

        //Step 5
        //For advanced analysis we join the number of tweets and the number of price change warnings by stock
        //for the last half minute, we keep only the counts. We use this information to compute rolling correlations
        //between the tweets and the price changes

        DataStream<Tuple2<Integer, Integer>> tweetsAndWarning = warningsPerStock.join(tweetsPerStock)
                .where(new KeySelector<Count, String>() {
                    @Override
                    public String getKey(Count count) throws Exception {
                        return count.symbol;
                    }
                })
                .equalTo(new KeySelector<Count, String>() {
                    @Override
                    public String getKey(Count count) throws Exception {
                        return count.symbol;
                    }
                })
                .window(TumblingProcessingTimeWindows.of(Time.seconds(30)))
                .with(new JoinFunction<Count, Count, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> join(Count first, Count second) throws Exception {
                        return new Tuple2<Integer, Integer>(first.count, second.count);
                    }
                });

        DataStream<Double> rollingCorrelation = tweetsAndWarning
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(30)))
                .aggregate(new AggregateFunction<Tuple2<Integer, Integer>, ListAccumulator<Tuple2<Integer, Integer>>, Double>() {

                    @Override
                    public ListAccumulator<Tuple2<Integer, Integer>> createAccumulator() {
                        return new ListAccumulator<>();
                    }

                    @Override
                    public ListAccumulator<Tuple2<Integer, Integer>> add(Tuple2<Integer, Integer> integerIntegerTuple2,
                                                                         ListAccumulator<Tuple2<Integer, Integer>> tuple2ListAccumulator) {
                        tuple2ListAccumulator.add(integerIntegerTuple2);
                        return tuple2ListAccumulator;
                    }

                    @Override
                    public Double getResult(ListAccumulator<Tuple2<Integer, Integer>> tuple2ListAccumulator) {
                        double leftSum = 0;
                        double rightSum = 0;
                        double count = 0;

                        double cov = 0.;
                        double leftSd = 0.;
                        double rightSd = 0.;

                        //compute mean for both sides, save count
                        for (Tuple2<Integer, Integer> pair : tuple2ListAccumulator.getLocalValue()) {
                            leftSum += pair.f0;
                            rightSum += pair.f1;
                            count++;
                        }

                        double leftMean = leftSum / count;
                        double rightMean = rightSum / count;

                        //compute covariance & std. deviations
                        for (Tuple2<Integer, Integer> pair : tuple2ListAccumulator.getLocalValue()) {
                            cov += (pair.f0 - leftMean) * (pair.f1 - rightMean) / count;
                        }

                        for (Tuple2<Integer, Integer> pair : tuple2ListAccumulator.getLocalValue()) {
                            leftSd += Math.pow(pair.f0 - leftMean, 2) / count;
                            rightSd += Math.pow(pair.f1 - rightMean, 2) / count;
                        }
                        leftSd = Math.sqrt(leftSd);
                        rightSd = Math.sqrt(rightSd);

                        return cov / (leftSd * rightSd);
                    }

                    @Override
                    public ListAccumulator<Tuple2<Integer, Integer>> merge(ListAccumulator<Tuple2<Integer, Integer>> tuple2ListAccumulator, ListAccumulator<Tuple2<Integer, Integer>> acc1) {
                        return acc1;
                    }
                });

        if (fileOutput) {
            rollingCorrelation.writeAsText(outputPath, FileSystem.WriteMode.OVERWRITE);
        } else {
            rollingCorrelation.print();
        }

        env.execute("Stock stream");

    }

    // *************************************************************************
    // DATA TYPES
    // *************************************************************************

    public static class StockPrice implements Serializable {

        private static final long serialVersionUID = 1L;
        public String symbol;
        public Double price;

        public StockPrice() {
        }

        public StockPrice(String symbol, Double price) {
            this.symbol = symbol;
            this.price = price;
        }

        @Override
        public String toString() {
            return "StockPrice{" +
                    "symbol='" + symbol + '\'' +
                    ", count=" + price +
                    '}';
        }
    }

    public static class Count implements Serializable{

        private static final long serialVersionUID = 1L;
        public String symbol;
        public Integer count;

        public Count() {
        }

        public Count(String symbol, Integer count) {
            this.symbol = symbol;
            this.count = count;
        }

        @Override
        public String toString() {
            return "Count{" +
                    "symbol='" + symbol + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    // *************************************************************************
    // USER FUNCTIONS
    // *************************************************************************

    public final static class StockSource implements SourceFunction<StockPrice> {

        private Double price;
        private String symbol;
        private Integer sigma;

        public StockSource(String symbol, Integer sigma) {
            this.symbol = symbol;
            this.sigma = sigma;
        }
        @Override
        public void run(SourceContext<StockPrice> sourceContext) throws Exception {
            price = DEFAULT_PRICE;
            Random random = new Random();

            while (true) {
                price = price + random.nextGaussian() * sigma;
                sourceContext.collect(new StockPrice(symbol, price));
                Thread.sleep(random.nextInt(200));
            }

        }

        @Override
        public void cancel() {

        }
    }

    public static final class TweetSource extends RichSourceFunction<String> {

        private static final long serialVersionUID = 1L;
        private transient Random random;
        private transient StringBuilder stringBuilder;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            random = new Random();
            stringBuilder = new StringBuilder();
        }

        @Override
        public void run(SourceContext<String> sourceContext) throws Exception {
            stringBuilder.setLength(0);
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(" ");
                stringBuilder.append(SYMBOLS.get(random.nextInt(SYMBOLS.size())));
            }
            Thread.sleep(500);
            sourceContext.collect(stringBuilder.toString());

        }

        @Override
        public void cancel() {

        }
    }

    // *************************************************************************
    // UTIL METHODS
    // *************************************************************************

    private static boolean fileOutput = false;
    private static String hostName;
    private static int port;
    private static String outputPath;

    private static boolean parseParameters(String[] args) {

        // parse input arguments
        //if (args.length == 3) {
            fileOutput = true;
            hostName = "localhost";
            port = 8081;
            outputPath = "/Users/kunal/Downloads/flink-stock";
        //}
        /*else if (args.length == 2) {
            hostName = args[0];
            port = Integer.valueOf(args[1]);
        } else {
            System.err.println("Usage: StockPrices <hostname> <port> [<output path>]");
            return false;
        }*/
        return true;
    }

}