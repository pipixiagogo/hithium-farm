package com.bugull.hithiumfarmweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.maxActive}")
    private int maxTotal;
    @Value("${redis.maxIdle}")
    private int maxIdle;
    @Value("${redis.minIdle}")
    private int minIdle;
    @Value("${redis.maxWait}")
    private long maxWatiMillis;

    @Value("${redis.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${redis.timeout}")
    private int timeout;

    @Bean
    JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);//最大连接数，连接全部用完，进行等待
        poolConfig.setMinIdle(minIdle); //最小空余数
        poolConfig.setMaxIdle(maxIdle); //最大空余数
        poolConfig.setMaxWaitMillis(maxWatiMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        return poolConfig;
        //pool = new JedisPool("127.0.0.1",6379);
    }
    @Bean(name = "jedisPool")
    JedisPool jedisPool(JedisPoolConfig jedisPoolConfig){
        JedisPool pool = new JedisPool(jedisPoolConfig,host,port,timeout);
        return pool;
    }

}
