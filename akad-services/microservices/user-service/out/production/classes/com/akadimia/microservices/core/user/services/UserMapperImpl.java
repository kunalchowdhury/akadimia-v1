package com.akadimia.microservices.core.user.services;

import com.akadimia.core.user.User;
import com.akadimia.microservices.core.user.persistence.UserEntity;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-10T17:57:34+0530",
    comments = "version: 1.3.0.Beta2, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User entityToApi(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User user = new User();

        user.setUserId( entity.getUserId() );
        user.setFirstName( entity.getFirstName() );
        user.setLastName( entity.getLastName() );
        user.setEmailId( entity.getEmailId() );
        user.setState( entity.getState() );
        user.setCity( entity.getCity() );
        user.setZipCode( entity.getZipCode() );
        user.setInstructor( entity.isInstructor() );
        user.setBriefDescription( entity.getBriefDescription() );
        user.setYouTubeURL( entity.getYouTubeURL() );
        user.setPhone( entity.getPhone() );

        return user;
    }

    @Override
    public UserEntity apiToEntity(User api) {
        if ( api == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUserId( api.getUserId() );
        userEntity.setFirstName( api.getFirstName() );
        userEntity.setLastName( api.getLastName() );
        userEntity.setEmailId( api.getEmailId() );
        userEntity.setState( api.getState() );
        userEntity.setCity( api.getCity() );
        userEntity.setZipCode( api.getZipCode() );
        userEntity.setInstructor( api.isInstructor() );
        userEntity.setBriefDescription( api.getBriefDescription() );
        userEntity.setYouTubeURL( api.getYouTubeURL() );
        userEntity.setPhone( api.getPhone() );

        return userEntity;
    }
}
