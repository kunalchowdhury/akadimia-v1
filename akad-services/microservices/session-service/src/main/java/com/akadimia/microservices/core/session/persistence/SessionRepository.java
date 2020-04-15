package com.akadimia.microservices.core.session.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SessionRepository extends ReactiveCrudRepository<SessionEntity, String> {
    Mono<SessionEntity> findBySessionId(String sessionId);
}
