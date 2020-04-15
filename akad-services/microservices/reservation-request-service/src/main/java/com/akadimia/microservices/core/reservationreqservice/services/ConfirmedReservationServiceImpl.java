package com.akadimia.microservices.core.reservationreqservice.services;

import com.akadimia.core.reservation.ConfirmedReservation;
import com.akadimia.core.reservation.ConfirmedReservationService;
import com.akadimia.microservices.core.reservationreqservice.persistence.ConfirmedReservationRepository;
import com.akadimia.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

@RestController
@ComponentScan("com.akadimia")

public class ConfirmedReservationServiceImpl implements ConfirmedReservationService {

    private final ConfirmedReservationRepository repository;

    private final ConfirmedReservationMapper mapper;

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public ConfirmedReservationServiceImpl(ConfirmedReservationRepository repository,
                                           ConfirmedReservationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ConfirmedReservation> getSession(String crid) {
        return repository.findByCrid(crid).
                switchIfEmpty(error(new NotFoundException("No confirmed reservation found for crid: " + crid)))
                .log()
                .map(e -> mapper.entityToApi(e));
    }
}
