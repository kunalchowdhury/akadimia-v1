package com.akadimia.msg.ser;

import com.akadimia.entity.user.ReservationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class ReservationRequestDeserializer implements DeserializationSchema<ReservationRequest> {
    private ObjectMapper mapper ;

    public  ReservationRequestDeserializer() {
        this.mapper = new ObjectMapper();
    }
    @Override
    public ReservationRequest deserialize(byte[] bytes) throws IOException {
        ReservationRequest resReq = null;
        try {
            resReq = mapper.readValue(bytes, ReservationRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resReq;
    }

    @Override
    public boolean isEndOfStream(ReservationRequest reservationRequest) {
        return false;
    }

    @Override
    public TypeInformation<ReservationRequest> getProducedType() {
        return null;
    }
}
