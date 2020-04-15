package com.akadimia.core.reservation;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface ConfirmedReservationService {

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(
            value    = "/confirmedReservation/{crid}",
            produces = "application/json")
    Mono<ConfirmedReservation> getSession(@PathVariable String crid);

}
