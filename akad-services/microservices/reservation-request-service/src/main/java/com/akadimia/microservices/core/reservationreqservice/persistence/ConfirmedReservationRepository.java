package com.akadimia.microservices.core.reservationreqservice.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConfirmedReservationRepository extends ReactiveCrudRepository<ConfirmedReservationEntity, String> {
    Mono<ConfirmedReservationEntity> findByCrid(String crid);

}
