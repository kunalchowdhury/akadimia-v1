package com.akadimia.microservices.core.user.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {
    Mono<UserEntity> findByUserId(String userId);
    Mono<UserEntity> findByEmailId(String emailId);
    Mono<UserEntity> findById(String id);

}
