package com.akadimia.msg.ser;

import com.akadimia.entity.user.InstructorSessionRegistration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class InstructorSessionDeserializer implements DeserializationSchema<InstructorSessionRegistration> {

    private ObjectMapper mapper ;

    public InstructorSessionDeserializer() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public InstructorSessionRegistration deserialize(byte[] bytes) throws IOException {
        InstructorSessionRegistration sess = null;
        try {
            sess = mapper.readValue(bytes, InstructorSessionRegistration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sess;
    }

    @Override
    public boolean isEndOfStream(InstructorSessionRegistration instructorSessionRegistration) {
        return false;
    }

    @Override
    public TypeInformation<InstructorSessionRegistration> getProducedType() {
        return null;
    }
}
