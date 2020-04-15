package com.akadimia.microservices.core.user;

import com.akadimia.core.session.Session;
import com.akadimia.core.user.User;
import com.akadimia.microservices.core.user.persistence.UserEntity;
import com.akadimia.microservices.core.user.services.UserMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MapperTests {

    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        User api = new User("u123", "Kunal" , "Chowdhury", "kunalkumar.chowdhury@gmail.com",
                "Karnakata", "Bangalore", 719009, 9742652223L, true, "abragasdkk akkkk a", "www.youtube.com");

        UserEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getUserId(), entity.getUserId());
        assertEquals(api.getFirstName(), entity.getFirstName());
        assertEquals(api.getLastName(), entity.getLastName());
        assertEquals(api.getEmailId(), entity.getEmailId());
        assertEquals(api.getState(), entity.getState());
        assertEquals(api.getCity(), entity.getCity());
        assertEquals(api.getYouTubeURL(), entity.getYouTubeURL());
        assertEquals(api.getZipCode(), entity.getZipCode());
        assertEquals(api.getBriefDescription(), entity.getBriefDescription());
        assertEquals(api.isInstructor(), entity.isInstructor());

        User api2 = mapper.entityToApi(entity);

        assertEquals(api2.getUserId(), entity.getUserId());
        assertEquals(api2.getFirstName(), entity.getFirstName());
        assertEquals(api2.getEmailId(), entity.getEmailId());
        assertEquals(api2.getState(), entity.getState());
        assertEquals(api2.getCity(), entity.getCity());
        assertEquals(api2.getYouTubeURL(), entity.getYouTubeURL());
        assertEquals(api2.getZipCode(), entity.getZipCode());
        assertEquals(api2.getBriefDescription(), entity.getBriefDescription());
        assertEquals(api2.isInstructor(), entity.isInstructor());


    }
}
