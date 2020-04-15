package com.akadimia.core.session;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionService {
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/sessionc/", produces = "application/json")
    Mono<String> putSessionc(
            @RequestParam(value = "subject", required = true) String subject,
            @RequestParam(value = "fromDate", required = true) long fromDate,
            @RequestParam(value = "toDate", required = true) long toDate,
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "area", required = true) String area,
            @RequestParam(value = "lat", required = true) double lat,
            @RequestParam(value = "lon", required = true) double lon,
            @RequestParam(value = "youTubeURL", required = true) String youTubeURL,
            @RequestParam(value = "sessionId", required = false, defaultValue = "undefined") String sessionId

    );


    Mono<Session> createSession(@RequestBody Session body);

    /**
     * Sample usage: curl $HOST:$PORT/session/1
     *
     * @param sessionId
     * @return the session, if found, else null
     */
    @GetMapping(
            value    = "/session/{sessionId}",
            produces = "application/json")
    Mono<Session> getSession(@PathVariable String sessionId);

    void deleteSession(@PathVariable String sessionId);

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(path = "/session/searchInstructor", method = RequestMethod.GET)
    Flux<Session> getSessions(@RequestParam(value = "subject", required = true) String subject,
                              @RequestParam(value = "fromDate", required = true) long fromDate,
                              @RequestParam(value = "toDate", required = true) long toDate,
                              @RequestParam(value = "latitude", required = true) double latitude,
                              @RequestParam(value = "longitude", required = true) double longitude,
                              @RequestParam(value = "radius", required = false, defaultValue = "20") double radius
                              );


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(path = "/sessionOnline/searchInstructor", method = RequestMethod.GET)
    Flux<OnlineSession> getOnlineSession(@RequestParam(value = "subject", required = true) String subject,
                              @RequestParam(value = "fromDate", required = true) long fromDate,
                              @RequestParam(value = "toDate", required = true) long toDate

    );

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/bookingRequest/", produces = "application/json")
    Mono<String> putBookingRequest(
            @RequestParam(value = "userId", required = true) String userId,
            @RequestParam(value = "sessionId", required = true) String sessionId);

}
