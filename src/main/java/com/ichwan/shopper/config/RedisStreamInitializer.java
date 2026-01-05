package com.ichwan.shopper.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamInitializer {

    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void createEmailGroupIfNotExists() {
        try {
            redisTemplate.opsForStream().createGroup(
                    "notif.email.stream",
                    ReadOffset.from("0-0"),
                    "email-group"
            );
        } catch (Exception e) {
            log.error("failed to start create consumer group: {}",e.getMessage());
        }
    }
}
