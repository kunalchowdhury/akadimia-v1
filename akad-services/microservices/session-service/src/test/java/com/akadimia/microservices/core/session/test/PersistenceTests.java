package com.akadimia.microservices.core.session.test;

import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.session.persistence.SessionEntity;
import com.akadimia.microservices.core.session.persistence.SessionRepository;
import com.akadimia.microservices.core.session.services.SessionMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private SessionRepository repository;

    private SessionEntity savedEntity;

    private SessionMapper mapper = Mappers.getMapper(SessionMapper.class);

    @Before
   	public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();


        Session api =new Session("lifesciences",
                start ,
                end,
                "kunalkumar.chowdhury@gmail.com",
                "Bangalore",
                new double[]{3.02, 4.50},
                "www.youtube.com",
                "xtest123");

        SessionEntity entity = mapper.apiToEntity(api);

        StepVerifier.create(repository.save(entity))
            .expectNextMatches(createdEntity -> {
                savedEntity = createdEntity;
                return areProductEqual(entity, savedEntity);
            })
            .verifyComplete();
    }


    @Test
   	public void create() {

        SessionEntity newEntity =new SessionEntity("lifesciences",
                System.currentTimeMillis() ,
                System.currentTimeMillis(),
                "kunalkumar.chowdhury@gmail.com",
                "Bangalore",
                new double[]{3.02, 4.50},
                "www.youtube.com",
                "x123");


        StepVerifier.create(repository.save(newEntity))
            .expectNextMatches(createdEntity -> newEntity.getSessionId() == createdEntity.getSessionId())
            .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
            .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
            .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
   	public void update() {
        savedEntity.setUserId("n2");
        StepVerifier.create(repository.save(savedEntity))
            .expectNextMatches(updatedEntity -> updatedEntity.getUserId().equals("n2"))
            .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                foundEntity.getVersion() == 1 &&
                foundEntity.getUserId().equals("n2"))
            .verifyComplete();
    }

    @Test
   	public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
   	public void getByProductId() {

        StepVerifier.create(repository.findBySessionId(savedEntity.getSessionId()))
            .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
            .verifyComplete();
    }

    @Test
   	public void duplicateError() {
        SessionEntity entity =new SessionEntity("lifesciences",
                System.currentTimeMillis() ,
                System.currentTimeMillis(),
                "kunalkumar.chowdhury@gmail.com",
                "Bangalore",
                new double[]{3.02, 4.50},
                "www.youtube.com",
                savedEntity.getSessionId());


        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        SessionEntity entity1 = repository.findById(savedEntity.getId()).block();
        SessionEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setUserId("n1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                foundEntity.getVersion() == 1 &&
                foundEntity.getUserId().equals("n1"))
            .verifyComplete();
    }

    private boolean areProductEqual(SessionEntity expectedEntity, SessionEntity actualEntity) {
        return
            (expectedEntity.getArea().equals(actualEntity.getArea())) &&
            (Arrays.equals(expectedEntity.getPosition(), actualEntity.getPosition())) &&
            (expectedEntity.getSessionId().equals(actualEntity.getSessionId())) &&
            (expectedEntity.getUserId().equals(actualEntity.getUserId())) &&
            (expectedEntity.getYouTubeURL().equals(actualEntity.getYouTubeURL())) &&
                    (expectedEntity.getSubject().equals(actualEntity.getSubject())) &&
                    (expectedEntity.getFromDate() == actualEntity.getFromDate()) &&
                    (expectedEntity.getToDate() == actualEntity.getToDate());
    }
}
