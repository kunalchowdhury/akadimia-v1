package com.akadimia.msg.ser;

import com.akadimia.entity.user.ReservationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ReservationRequestSerializer implements Serializer<ReservationRequest> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, ReservationRequest reservationRequest) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] retVal = null;
        try {
            retVal = mapper.writeValueAsString(reservationRequest).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;

    }

    @Override
    public void close() {

    }
}
