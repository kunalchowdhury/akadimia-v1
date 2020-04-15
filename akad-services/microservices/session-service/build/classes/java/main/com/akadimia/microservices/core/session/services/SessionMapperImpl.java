package com.akadimia.microservices.core.session.services;

import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.session.persistence.SessionEntity;
import java.util.Arrays;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class SessionMapperImpl implements SessionMapper {

    @Override
    public Session entityToApi(SessionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Session session = new Session();

        session.setSubject( entity.getSubject() );
        session.setFromDate( entity.getFromDate() );
        session.setToDate( entity.getToDate() );
        session.setUserId( entity.getUserId() );
        session.setArea( entity.getArea() );
        double[] position = entity.getPosition();
        if ( position != null ) {
            session.setPosition( Arrays.copyOf( position, position.length ) );
        }
        session.setYouTubeURL( entity.getYouTubeURL() );
        session.setSessionId( entity.getSessionId() );
        session.setReserved( entity.getReserved() );

        return session;
    }

    @Override
    public SessionEntity apiToEntity(Session api) {
        if ( api == null ) {
            return null;
        }

        SessionEntity sessionEntity = new SessionEntity();

        sessionEntity.setSubject( api.getSubject() );
        sessionEntity.setFromDate( api.getFromDate() );
        sessionEntity.setToDate( api.getToDate() );
        sessionEntity.setUserId( api.getUserId() );
        sessionEntity.setArea( api.getArea() );
        double[] position = api.getPosition();
        if ( position != null ) {
            sessionEntity.setPosition( Arrays.copyOf( position, position.length ) );
        }
        sessionEntity.setYouTubeURL( api.getYouTubeURL() );
        sessionEntity.setSessionId( api.getSessionId() );
        sessionEntity.setReserved( api.getReserved() );

        return sessionEntity;
    }
}
