package com.ichwan.shopper.config;

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

    @Value("${redis.timeout}")
    private int timeout;

    @Value("$redis.jedis.max-total")
    private int maxTotal;

    @Value("${redis.jedis.max-idle}")
    private int maxIdle;

    @Value("${redis.jedis.min-idle}")
    private int minIdle;

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        return new JedisPool(config, host, port, timeout);
    }
}
