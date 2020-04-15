package com.akadimia.flow.stream.parsers;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class ReservedTutorStreamParser implements FlatMapFunction<String, Tuple2<String, Boolean>> {
    @Override
    public void flatMap(String s, Collector<Tuple2<String, Boolean>> collector) {
        String[] strVals = s.split("=");
        if (strVals.length == 2) {
            String key = strVals[0];
            Boolean val = "true".equals(strVals[1]);
            collector.collect(Tuple2.of(key, val));
        }
    }

}
