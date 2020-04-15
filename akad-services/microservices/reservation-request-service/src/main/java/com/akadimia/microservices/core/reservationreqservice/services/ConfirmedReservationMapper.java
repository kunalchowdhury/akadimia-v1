package com.akadimia.microservices.core.reservationreqservice.services;

import com.akadimia.core.reservation.ConfirmedReservation;
import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.reservationreqservice.persistence.ConfirmedReservationEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ConfirmedReservationMapper {

    @Mappings({
            //@Mapping(target = "serviceAddress", ignore = true)
    })
    ConfirmedReservation entityToApi(ConfirmedReservationEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ConfirmedReservationEntity apiToEntity(ConfirmedReservation api);
}
