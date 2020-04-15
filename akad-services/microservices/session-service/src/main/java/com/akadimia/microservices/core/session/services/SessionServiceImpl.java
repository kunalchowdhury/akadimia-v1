package com.akadimia.microservices.core.session.services;

import com.akadimia.core.session.OnlineSession;
import com.akadimia.core.session.Session;
import com.akadimia.core.session.SessionService;
import com.akadimia.core.user.User;
import com.akadimia.entity.user.ReservationRequest;
import com.akadimia.messaging.Producer;
import com.akadimia.microservices.core.session.persistence.SessionEntity;
import com.akadimia.microservices.core.session.persistence.SessionRepository;
import com.akadimia.util.exceptions.InvalidInputException;
import com.akadimia.util.exceptions.NotFoundException;
import com.akadimia.util.http.ServiceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import static reactor.core.publisher.Mono.error;

@RestController
@ComponentScan("com.akadimia")
public class SessionServiceImpl implements SessionService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final ServiceUtil serviceUtil;

    //@Autowired
    private final SessionRepository repository;

    private final SessionMapper mapper;

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private Producer producer;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    public SessionServiceImpl(SessionRepository repository, SessionMapper mapper, ServiceUtil serviceUtil, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<String> putSessionc(String subject, long fromDate, long toDate, String userId, String area, double lat, double lon, String youTubeURL, String sessionId) {
        Mono<Session> ret=  createSession(new Session(subject, fromDate,
                toDate, userId, area, new double[]{lat, lon}, youTubeURL,"undefined".equals(sessionId) ? UUID.randomUUID().toString() : sessionId, "N"));

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
    public Mono<Session> createSession(Session body) {

        //create user id - unique;
        if (body.getSessionId() == null || body.getUserId().isEmpty()) throw new InvalidInputException("Invalid userId: " + body.getUserId());

        SessionEntity sessionEntity = mapper.apiToEntity(body);
        Mono<Session> newEntity = repository.save(sessionEntity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Session Id: " + body.getSessionId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Mono<Session> getSession(String sessionId) {

        if (sessionId == null || sessionId.trim().isEmpty()) throw new InvalidInputException("Invalid sessionId:" );

        Mono<SessionEntity> bySessionId = repository.findBySessionId(sessionId);
        return bySessionId
            .switchIfEmpty(error(new NotFoundException("No session found for sessionId: " + sessionId)))
            .log()
            .map(e -> mapper.entityToApi(e));

    }

    @Override
    public void deleteSession(String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) throw new InvalidInputException("Invalid sessionId: " + sessionId);

        LOG.debug("deleteSession: tries to delete an entity with sessionId: {}", sessionId);
        repository.findBySessionId(sessionId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
    }

    @Override
    public Flux<Session> getSessions(String subject, long fromDate, long toDate, double latitude, double longitude, double radius) {
        Point point = new Point(latitude, longitude);
        Distance distance = new Distance(radius, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);


        Instant si = Instant.ofEpochMilli(fromDate);
        Instant ei = Instant.ofEpochMilli(toDate);

        Instant low = si.minus(2, ChronoUnit.HOURS);
        Instant high = ei.plus(2, ChronoUnit.HOURS);

        Flux<Session> flux = reactiveMongoTemplate.find(Query.query(Criteria.where("subject").is(subject))
                        .addCriteria(Criteria.where("fromDate").gte(low.toEpochMilli()))
                        .addCriteria(Criteria.where("toDate").lte(high.toEpochMilli()))
                        .addCriteria(Criteria.where("position").withinSphere(circle)).limit(1000),
                SessionEntity.class).map(e -> mapper.entityToApi(e)).sort(Comparator.comparing(Session::getSubject));

        return flux;


    }

    @Override
    public Flux<OnlineSession> getOnlineSession(String subject, long fromDate, long toDate) {
        Instant si = Instant.ofEpochMilli(fromDate);
        Instant ei = Instant.ofEpochMilli(toDate);

        Instant low = si.minus(2, ChronoUnit.HOURS);
        Instant high = ei.plus(2, ChronoUnit.HOURS);

        Flux<OnlineSession> flux = reactiveMongoTemplate.find(Query.query(Criteria.where("subject").is(subject))
                        .addCriteria(Criteria.where("fromDate").gte(low.toEpochMilli()))
                        .addCriteria(Criteria.where("toDate").gte(low.toEpochMilli()))
                        .addCriteria(Criteria.where("reserved").is("N"))
                        .limit(1000),
                SessionEntity.class).map(e ->
        {
            Session e1 =mapper.entityToApi(e);
            final String uri = "https://"+env.getProperty("host.name")+":7002/user/"+e1.getUserId();

            RestTemplate restTemplate = new RestTemplate();
            User result;
            try {
                result = restTemplate.getForObject(uri, User.class);
            }catch (Throwable t){
                t.printStackTrace();
                return new OnlineSession();
            }

            Calendar myCur = Calendar.getInstance();
            myCur.setTimeInMillis(e1.getFromDate());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String utcTime = simpleDateFormat.format(myCur.getTime()).replace(" ", "T")+"Z";

            Instant s = Instant.parse(utcTime);
            LocalDateTime l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));
            String st = l.toString();

            myCur.setTimeInMillis(e1.getToDate());
            utcTime = simpleDateFormat.format(myCur.getTime()).replace(" ", "T")+"Z";

            s = Instant.parse(utcTime);
            l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));

            String ste = l.toString();

            OnlineSession os = new OnlineSession(subject, result.getFirstName() +" " +result.getLastName(), st, ste, e1.getYouTubeURL(), e1.getSessionId() );
            return os;

        });

        return flux;

    }

    @Override
    public Mono<String> putBookingRequest(String emailId, String sessionId) {
        String uri = "https://"+env.getProperty("host.name")+":7002/userEntityMailId/"+emailId;

        RestTemplate restTemplate = new RestTemplate();
        User user = getUser(uri, restTemplate);
        if (user== null) return Mono.empty();

        Mono<SessionEntity> bySessionId = repository.findBySessionId(sessionId);
        Mono<String> map = bySessionId
                .switchIfEmpty(error(new NotFoundException("No session found for sessionId: " + sessionId)))
                .log()
                .map(e -> {
                    Session session = mapper.entityToApi(e);
                    String uriInst = "https://"+env.getProperty("host.name")+":7002/user/" + Objects.requireNonNull(session).getUserId();
                    User instructor = getUser(uriInst, restTemplate);

                    if (instructor == null) return "FAILED";

                    Calendar myCur = Calendar.getInstance();
                    myCur.setTimeInMillis(session.getFromDate());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String utcTime = simpleDateFormat.format(myCur.getTime()).replace(" ", "T")+"Z";

                    Instant s = Instant.parse(utcTime);
                    LocalDateTime l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));
                    String st = l.toString();

                    myCur.setTimeInMillis(session.getToDate());
                    utcTime = simpleDateFormat.format(myCur.getTime()).replace(" ", "T")+"Z";

                    s = Instant.parse(utcTime);
                    l = LocalDateTime.ofInstant(s, ZoneId.of("Asia/Kolkata"));

                    String ste = l.toString();

                    ReservationRequest request = new ReservationRequest(session.getSessionId(), instructor.getUserId(),
                            user.getUserId(), System.currentTimeMillis(),
                            user.getEmailId(), instructor.getEmailId(), true, "", session.getSubject(),
                            instructor.getFirstName()+" "+instructor.getLastName(), st, ste);


                    producer.sendMessage(session.getSubject(), request);
                    return "SUCCESS";

                });

        return map;
    }

    private User getUser(String uri, RestTemplate restTemplate) {
        User instructor;
        try {
            instructor = restTemplate.getForObject(uri, User.class);
        }catch (Throwable t){
            t.printStackTrace();
            return null;
        }
        return instructor;
    }


}