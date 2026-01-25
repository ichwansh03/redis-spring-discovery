package com.ichwan.shopper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession()
public class RedisSessionConfig {

    @Bean
    public RedisSerializer<Object> sessionRedisSerializer() {
        return new JdkSerializationRedisSerializer();
    }
}
