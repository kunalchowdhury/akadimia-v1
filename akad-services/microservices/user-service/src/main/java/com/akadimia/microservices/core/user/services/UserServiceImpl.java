package com.akadimia.microservices.core.user.services;

import com.akadimia.core.user.User;
import com.akadimia.core.user.UserService;
import com.akadimia.microservices.core.user.persistence.UserEntity;
import com.akadimia.microservices.core.user.persistence.UserRepository;
import com.akadimia.util.exceptions.InvalidInputException;
import com.akadimia.util.exceptions.NotFoundException;
import com.akadimia.util.http.ServiceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static reactor.core.publisher.Mono.error;

@RestController
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final UserRepository repository;

    private final UserMapper mapper;

    private UUID uuid;
    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<String> putUserc(String userId, String firstName, String lastName, String emailId, String state, String city, int zipCode, long phone,
                                 boolean instructor, String briefDescription, String youTubeURL) {
        Mono<User> ret=  createUser(new User("undefined".equals(userId) ? UUID.randomUUID().toString() : userId, firstName, lastName,
                emailId, state, city, zipCode, phone, instructor, briefDescription, youTubeURL));

        return ret.map(e1 -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(e1);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "";
        });


    }

    @Override
    public Mono<User> createUser(User body) {

        //create user id - unique;
        if (body.getUserId() == null || body.getUserId().isEmpty()) throw new InvalidInputException("Invalid userId: " + body.getUserId());

        UserEntity entity = mapper.apiToEntity(body);
        Mono<User> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, User Id: " + body.getUserId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Mono<User> getUser(String userId) {

        if (userId == null || userId.trim().isEmpty()) throw new InvalidInputException("Invalid userId:");

        return repository.findByUserId(userId)
            .switchIfEmpty(error(new NotFoundException("No user found for userId: " + userId)))
            .log()
            .map(e -> mapper.entityToApi(e));

    }

    @Override
    public Mono<String> getUserByEmail(String mailId) {
        if (mailId == null || mailId.trim().isEmpty()) throw new InvalidInputException("Invalid emailId:");
        return repository.findByEmailId(mailId)
                .switchIfEmpty(error(new NotFoundException("No user found for emailId: " + mailId)))
                .log()
                .map(e -> mapper.entityToApi(e).getUserId());
    }

    @Override
    public Mono<User> getUserEntityByEmail(String mailId) {
        if (mailId == null || mailId.trim().isEmpty()) throw new InvalidInputException("Invalid emailId:");
        return repository.findByEmailId(mailId)
                .switchIfEmpty(error(new NotFoundException("No user found for emailId: " + mailId)))
                .log()
                .map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<String> getUserById(String id) {
        if (id == null || id.trim().isEmpty()) throw new InvalidInputException("Invalid user id:");
        return repository.findById(id)
                .switchIfEmpty(error(new NotFoundException("No user found for emailId: " + id)))
                .log()
                .map(e -> mapper.entityToApi(e).getUserId());
    }

    @Override
    public void deleteUser(String userId) {

        if (userId == null || userId.isEmpty()) throw new InvalidInputException("Invalid userId: " + userId);

        LOG.debug("deleteUser: tries to delete an entity with userId: {}", userId);
        repository.findByUserId(userId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
    }


}