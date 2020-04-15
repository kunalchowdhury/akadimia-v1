package com.akadimia.flow.stream.parsers;

import com.akadimia.entity.user.ReservationRequest;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

public class RRInstFilterJoinProcessFunction extends CoProcessFunction<ReservationRequest ,Tuple2<String,Boolean>, ReservationRequest> {

    public static class FilterState{
        public FilterState(){

        }
        public FilterState(Boolean active){
            this.active = active;
        }
        public Boolean active;
    }


    private ValueState<FilterState> state;

    @Override
    public void open(Configuration parameters) throws Exception{
        ValueStateDescriptor<FilterState> desc = new ValueStateDescriptor<FilterState>(
                "filterState",
                FilterState.class, new FilterState(true)
        );
        state = getRuntimeContext().getState(desc);
    }

    @Override
    public void processElement1(ReservationRequest input, Context context, Collector<ReservationRequest> collector) throws Exception {
        FilterState current = state.value();
        input.setShouldProcess(current.active);
        //if(current.active) collector.collect(input);//filtering place
        collector.collect(input);
    }

    @Override
    public void processElement2(Tuple2<String, Boolean> input, Context context, Collector<ReservationRequest> collector) throws Exception {
        FilterState current = state.value();
        current.active = input.f1;//set state value
        state.update(current);
    }


}
