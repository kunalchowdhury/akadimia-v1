package com.akadimia.msg.ser;

import com.akadimia.entity.user.InstructorSessionRegistration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class InstructorSessionSerializer implements SerializationSchema<InstructorSessionRegistration> {
    private ObjectMapper mapper ;

    public InstructorSessionSerializer() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(InstructorSessionRegistration instructorSessionRegistration) {
        try {
            return mapper.writeValueAsString(instructorSessionRegistration).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
