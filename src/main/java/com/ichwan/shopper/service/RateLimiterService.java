package com.ichwan.shopper.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private DefaultRedisScript<Long> rateLimiterScript;

    @PostConstruct
    private void init() {
        rateLimiterScript = new DefaultRedisScript<>();
        rateLimiterScript.setLocation(new ClassPathResource("scripts/rate-limiter.lua"));
        rateLimiterScript.setResultType(Long.class);
    }

    /**
     * Executes the rate-limiter Lua script.
     * @param key the redis key used for rate limiting
     * @param expireSeconds expiration window in seconds
     * @return current counter value after increment
     */
    public Long hit(String key, long expireSeconds) {
        return redisTemplate.execute(rateLimiterScript, Collections.singletonList(key), String.valueOf(expireSeconds));
    }
}
