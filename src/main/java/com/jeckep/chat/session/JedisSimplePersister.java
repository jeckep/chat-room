package com.jeckep.chat.session;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JedisSimplePersister implements Persister {
    private Jedis jedis;

    public JedisSimplePersister(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public Map<String, Object> restore(String sessionCookieValue, int expire) {
        if(jedis.exists(sessionCookieValue.getBytes())){
            jedis.expire(sessionCookieValue.getBytes(), expire);
        }
        byte[] value = jedis.get(sessionCookieValue.getBytes());
        if (value != null) {
            try {
                Map<String, Object> attrs = (Map<String, Object>) convertFromBytes(value);
                return attrs;
            } catch (IOException | ClassNotFoundException e) {
                log.error("Cannot convert byte[] to session attrs", e);
            }
        }
        return new HashMap<>();
    }

    @Override
    public void save(String sessionCookieValue, Map<String, Object> sessionAttrs, int expire) {
        try {
            if(!sessionAttrs.isEmpty()){
                byte[] value = convertToBytes(sessionAttrs);
                jedis.set(sessionCookieValue.getBytes(), value);
            }

            jedis.expire(sessionCookieValue.getBytes(), expire);
        } catch (IOException e) {
            log.error("Cannot convert session attrs to byte[]", e);
        }
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
