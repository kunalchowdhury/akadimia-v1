package com.akadimia.core.user;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface UserService {
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/userc/", produces = "application/json")
    Mono<String> putUserc(
                       @RequestParam(value = "userId", required = false, defaultValue = "undefined") String userId,
                       @RequestParam(value = "firstName", required = true) String firstName,
                       @RequestParam(value = "lastName", required = true) String lastName,
                       @RequestParam(value = "emailId", required = true) String emailId,
                       @RequestParam(value = "state", required = true) String state,
                       @RequestParam(value = "city", required = true) String city,
                       @RequestParam(value = "zipCode", required = true) int zipCode,
                       @RequestParam(value = "phone", required = true) long phone,
                       @RequestParam(value = "instructor", required = true) boolean instructor,
                       @RequestParam(value = "briefDescription", required = false) String briefDescription,
                       @RequestParam(value = "youTubeURL", required = true) String youTubeURL
    );

    Mono<User> createUser(@RequestBody User body);

    /**
     * Sample usage: curl $HOST:$PORT/user/1
     *
     * @param userId
     * @return the user, if found, else null
     */
    @GetMapping(
            value    = "/user/{userId}",
            produces = "application/json")
    Mono<User> getUser(@PathVariable String userId);

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(
            value    = "/userMailId/{mailId}",
            produces = "application/json")
    Mono<String> getUserByEmail(@PathVariable String mailId);


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(
            value    = "/userEntityMailId/{mailId}",
            produces = "application/json")
    Mono<User> getUserEntityByEmail(@PathVariable String mailId);


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(
            value    = "/userId/{id}",
            produces = "application/json")
    Mono<String> getUserById(@PathVariable String id);


    void deleteUser(@PathVariable String userId);
}