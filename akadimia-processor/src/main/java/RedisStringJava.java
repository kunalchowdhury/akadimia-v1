import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisStringJava {
    static class A{
        String name;
        long val;
        double dval ;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getVal() {
            return val;
        }

        public void setVal(long val) {
            this.val = val;
        }

        public double getDval() {
            return dval;
        }

        public void setDval(double dval) {
            this.dval = dval;
        }

        @Override
        public String toString() {
            return "A{" +
                    "name='" + name + '\'' +
                    ", val=" + val +
                    ", dval=" + dval +
                    '}';
        }
    }

/*
    static <T> Map<String, String> getMap(T t) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        Field[] declaredFields = t.getClass().getDeclaredFields();
        for (Field f : declaredFields){
            if(f.getType() == long.class){
                map.put(f.getName(), String.valueOf(f.getLong((Object) t)));
            }else if(f.getType() == String.class){
                map.put(f.getName(), f.get((Object) t).toString());
            }else if(f.getType() == double.class){
                map.put(f.getName(), String.valueOf(f.get((Object) t)));
            }
        }

        return map;
    }
*/

    public static void main( String[] args )
    {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        // operations with Redis based Lock
        // implements java.util.concurrent.locks.Lock
        RReadWriteLock lock = redisson.getReadWriteLock("simpleLock");
        RLock writeLock = lock.writeLock();
        writeLock.lock(2000, TimeUnit.MILLISECONDS);
        try {
            // do some actions
            RMapCache<Object, Object> mapCache = redisson.getMapCache("mapCache123");
         //   mapCache.put("mapKey1", "This is a map value123");
            String mapValue = mapCache.get("mapKey1").toString();
            System.out.println("stored map value: " + mapValue);
        } finally {
            writeLock.unlock();
        }
        // operations with Redis based Map
        // implements java.util.concurrent.ConcurrentMap
       // RMap<String, String> map = redisson.getMap("simpleMap");

        redisson.shutdown();
    }

    public static void main1(String[] args) throws IllegalAccessException {
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //set the data in redis string
        jedis.set("tutorial-name", "Redis tutorial");
        // Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));


        A a = new A();
        a.name ="Kunal";
        a.val= 120l;
        a.dval=202.34;

       /* Map<String, String> m = getMap(a);
        System.out.println(m);*/
    }
}
