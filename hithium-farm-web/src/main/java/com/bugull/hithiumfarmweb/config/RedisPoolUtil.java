package com.bugull.hithiumfarmweb.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Component
public class RedisPoolUtil {
    @Resource(name = "jedisPool")
    private JedisPool jedisPool;


    @Value("${redis.password}")
    private String password;

    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        if (!StringUtils.isEmpty(password)) {
            jedis.auth(password);
        }
        return jedis;
    }

}
