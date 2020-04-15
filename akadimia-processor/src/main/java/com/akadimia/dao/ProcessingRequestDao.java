package com.akadimia.dao;

import com.akadimia.entity.user.Entity;
import com.akadimia.entity.user.InstructorSessionRegistration;
import com.akadimia.entity.user.ReservationRequest;
import com.google.common.collect.Maps;
import com.mongodb.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ProcessingRequestDao {
    public enum Status {SUCCESS, FAILED}
    /*private Jedis jedis;
    private RedissonClient redisson;
    private RReadWriteLock lock;

    public ProcessingRequestDao() {
        //this.jedis = new Jedis(serverAddress);
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        redisson = Redisson.create(config);
        // operations with Redis based Lock
        // implements java.util.concurrent.locks.Lock
        lock = redisson.getReadWriteLock("lock");
    }
*/
    private static <T> Map<String, String> getMap(T t) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        Field[] declaredFields = t.getClass().getDeclaredFields();
        for (Field f : declaredFields){
            f.setAccessible(true);
            if(f.getType() == long.class){
                map.put(f.getName(), String.valueOf(f.getLong((Object) t)));
            }else if(f.getType() == String.class){
                if(f.get((Object) t) != null) {
                    map.put(f.getName(), f.get((Object) t).toString());
                }
            }else if(f.getType() == double.class){
                map.put(f.getName(), String.valueOf(f.get((Object) t)));
            }else if(f.getType() == Boolean.class){
                map.put(f.getName(), String.valueOf(f.get((Object) t)));
            }
        }
        return map;
    }

    private static <T extends Entity> boolean persist(RedissonClient redisson, RReadWriteLock lock, T t, String cacheName){
        RLock writeLock = lock.writeLock();
        writeLock.lock(2000, TimeUnit.MILLISECONDS);
        boolean retValue = false;
        try {
            // do some actions
            RMapCache<Object, Object> mapCache = redisson.getMapCache(cacheName);
            if(!mapCache.containsKey(t.getId())) {
                mapCache.put(t.getId(), getMap(t));
                retValue = true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
        return retValue;

    }

    public static Tuple2<String, Status> saveInstructorSession(RedissonClient redisson, RReadWriteLock lock, InstructorSessionRegistration instructorSessionRegistration){
        boolean persisted = persist(redisson, lock, instructorSessionRegistration, "instructor_session");
        if(persisted){
            return Tuple2.of(instructorSessionRegistration.getId(), Status.SUCCESS);
        }
        return Tuple2.of(instructorSessionRegistration.getId(), Status.FAILED);
    }

    public static Tuple2<Optional<Map<String, String>>, Status> saveReservationRequest(RedissonClient redisson, RReadWriteLock lock, ReservationRequest reservationRequest){
        boolean persisted = persist(redisson, lock, reservationRequest, "reservation_request");
        if(persisted){
         //   RMapCache<Object, Object> mapCache = redisson.getMapCache("instructor_session");
         //   Map<String, String> instructor = (Map<String, String>) mapCache.get(reservationRequest.getInstructorId());
            try {
                persist(reservationRequest);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return Tuple2.of(Optional.of(Maps.newHashMap()), Status.SUCCESS);
        }
        return Tuple2.of(Optional.empty(), Status.FAILED);
    }

    public static void close(RedissonClient redisson){
        redisson.shutdown();
    }

    private static void persist(ReservationRequest reservationRequest) throws UnknownHostException {

        DBObject doc = createDBObject(reservationRequest);

        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("akadimia");

        DBCollection col = db.getCollection("confirmed_reservations");
        col.insert(doc);


        DBCollection sessions = db.getCollection("sessions");
        BasicDBObject searchQuery = new BasicDBObject().append("sessionId", reservationRequest.getId());

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("reserved", "Y"));

        sessions.findAndModify(searchQuery, newDocument);

        mongo.close();
    }

    private static DBObject createDBObject(ReservationRequest reservationRequest) {
        BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

        int val = reservationRequest.getClassLink().lastIndexOf("/");
        docBuilder.append("crid", reservationRequest.getClassLink().substring(val + 1));
        docBuilder.append("userId", reservationRequest.getUserId());
        docBuilder.append("instId", reservationRequest.getInstructorId());
        docBuilder.append("subject", reservationRequest.getSubject());
        docBuilder.append("startTime", reservationRequest.getStartTime());
        docBuilder.append("endTime", reservationRequest.getEndTime());
        docBuilder.append("instructorName", reservationRequest.getInstructorName());
        docBuilder.append("sessionId", reservationRequest.getId());

        return docBuilder.get();
    }


}
