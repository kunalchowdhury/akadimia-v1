package com.akadimia.microservices.core.session.services;

import com.akadimia.core.session.Session;
import com.akadimia.microservices.core.session.persistence.SessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mappings({
            //@Mapping(target = "serviceAddress", ignore = true)
    })
    Session entityToApi(SessionEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    SessionEntity apiToEntity(Session api);
}
