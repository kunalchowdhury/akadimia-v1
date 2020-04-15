package com.akadimia.microservices.core.session.test;

import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.session.persistence.SessionEntity;
import com.akadimia.microservices.core.session.services.SessionMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class MapperTests {

    private SessionMapper mapper = Mappers.getMapper(SessionMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        Session api = new Session("lifesciences", start , end, "kunalkumar.chowdhury@gmail.com",
                "Bangalore", new double[]{3.02, 4.50}, "www.youtube.com", "x123");

        SessionEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getArea(), entity.getArea());
        assertEquals(api.getFromDate(), entity.getFromDate());
        assertEquals(api.getToDate(), entity.getToDate());
        assertEquals(api.getUserId(), entity.getUserId());
        assertArrayEquals(api.getPosition(), entity.getPosition(), 0);
        assertEquals(api.getYouTubeURL(), entity.getYouTubeURL());
        assertEquals(api.getSessionId(), entity.getSessionId());

        Session api2 = mapper.entityToApi(entity);

        assertEquals(api2.getArea(), entity.getArea());
        assertEquals(api2.getFromDate(), entity.getFromDate());
        assertEquals(api2.getToDate(), entity.getToDate());
        assertEquals(api2.getUserId(), entity.getUserId());
        assertArrayEquals(api2.getPosition(), entity.getPosition(),0);
        assertEquals(api2.getYouTubeURL(), entity.getYouTubeURL());
        assertEquals(api2.getSessionId(), entity.getSessionId());

    }
}
