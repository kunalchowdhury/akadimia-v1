package com.akadimia.microservices.core.user;

import com.akadimia.core.user.User;
import com.akadimia.microservices.core.user.persistence.UserEntity;
import com.akadimia.microservices.core.user.persistence.UserRepository;
import com.akadimia.microservices.core.user.services.UserMapper;
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

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private UserRepository repository;

    private UserEntity savedEntity;

    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Before
   	public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();


        User api = new User("u123", "Kunal" , "Chowdhury", "kunalkumar.chowdhury@gmail.com",
                "Karnakata", "Bangalore", 719009, 9742652223l, true, "abragasdkk akkkk a", "www.youtube.com");

        UserEntity entity = mapper.apiToEntity(api);

        StepVerifier.create(repository.save(entity))
            .expectNextMatches(createdEntity -> {
                savedEntity = createdEntity;
                return areProductEqual(entity, savedEntity);
            })
            .verifyComplete();
    }


    @Test
   	public void create() {

        UserEntity newEntity = new UserEntity("u1123", "Kunal" , "Chowdhury", "kunalkumar.chowdhury@gmail.com",
                "Karnakata", "Bangalore", 719009, 9742652223l,true, "abragasdkk akkkk a", "www.youtube.com");


        StepVerifier.create(repository.save(newEntity))
            .expectNextMatches(createdEntity -> newEntity.getUserId().equals(createdEntity.getUserId()))
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

        StepVerifier.create(repository.findByUserId(savedEntity.getUserId()))
            .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
            .verifyComplete();
    }

    @Test
   	public void duplicateError() {
        UserEntity entity= new UserEntity("u123", "Kunal" , "Chowdhury", "kunalkumar.chowdhury@gmail.com",
                "Karnakata", "Bangalore", 719009, 9742652223l, true, "abragasdkk akkkk a", "www.youtube.com");


        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        UserEntity entity1 = repository.findById(savedEntity.getId()).block();
        UserEntity entity2 = repository.findById(savedEntity.getId()).block();

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

    private boolean areProductEqual(UserEntity expectedEntity, UserEntity actualEntity) {
        return
            (expectedEntity.getUserId().equals(actualEntity.getUserId())) &&
                    (expectedEntity.getFirstName().equals(actualEntity.getFirstName()) &&
            (expectedEntity.getLastName().equals(actualEntity.getLastName())) &&
            (expectedEntity.getEmailId().equals(actualEntity.getEmailId())) &&
            (expectedEntity.getYouTubeURL().equals(actualEntity.getYouTubeURL())) &&
                    (expectedEntity.getState().equals(actualEntity.getState())) &&
                    (expectedEntity.getCity().equals(actualEntity.getCity())) &&
                            (expectedEntity.getBriefDescription().equals(actualEntity.getBriefDescription())) &&
                            (expectedEntity.isInstructor() && (actualEntity.isInstructor())) &&
                    (expectedEntity.getZipCode() == actualEntity.getZipCode()));
    }
}
