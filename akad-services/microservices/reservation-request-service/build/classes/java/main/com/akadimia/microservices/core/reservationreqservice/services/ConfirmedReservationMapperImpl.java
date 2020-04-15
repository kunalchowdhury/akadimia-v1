package com.akadimia.microservices.core.reservationreqservice.services;

import com.akadimia.core.reservation.ConfirmedReservation;
import com.akadimia.microservices.core.reservationreqservice.persistence.ConfirmedReservationEntity;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class ConfirmedReservationMapperImpl implements ConfirmedReservationMapper {

    @Override
    public ConfirmedReservation entityToApi(ConfirmedReservationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ConfirmedReservation confirmedReservation = new ConfirmedReservation();

        confirmedReservation.setCrid( entity.getCrid() );
        confirmedReservation.setUserId( entity.getUserId() );
        confirmedReservation.setInstId( entity.getInstId() );
        confirmedReservation.setSubject( entity.getSubject() );
        confirmedReservation.setStartTime( entity.getStartTime() );
        confirmedReservation.setEndTime( entity.getEndTime() );
        confirmedReservation.setInstructorName( entity.getInstructorName() );
        confirmedReservation.setSessionId( entity.getSessionId() );
        confirmedReservation.setFromDate( entity.getFromDate() );
        confirmedReservation.setToDate( entity.getToDate() );

        return confirmedReservation;
    }

    @Override
    public ConfirmedReservationEntity apiToEntity(ConfirmedReservation api) {
        if ( api == null ) {
            return null;
        }

        ConfirmedReservationEntity confirmedReservationEntity = new ConfirmedReservationEntity();

        confirmedReservationEntity.setCrid( api.getCrid() );
        confirmedReservationEntity.setUserId( api.getUserId() );
        confirmedReservationEntity.setInstId( api.getInstId() );
        confirmedReservationEntity.setSubject( api.getSubject() );
        confirmedReservationEntity.setStartTime( api.getStartTime() );
        confirmedReservationEntity.setEndTime( api.getEndTime() );
        confirmedReservationEntity.setInstructorName( api.getInstructorName() );
        confirmedReservationEntity.setSessionId( api.getSessionId() );
        confirmedReservationEntity.setFromDate( api.getFromDate() );
        confirmedReservationEntity.setToDate( api.getToDate() );

        return confirmedReservationEntity;
    }
}
