package com.ichwan.shopper.operations.bitmaps;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyActiveUserService {

    private final StringRedisTemplate redisTemplate;

    private String key(LocalDate date) {
        return "login:"+date;
    }

    public void markUserLogin(LocalDate date, long userId) {
        redisTemplate.opsForValue().setBit(key(date), userId, true);
    }

    public boolean isUserLoggedIn(LocalDate date, long userId) {
        return redisTemplate.opsForValue().getBit(key(date), userId);
    }

    public long countDailyActiveUsers(LocalDate date) {
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(key(date).getBytes()));

        return Optional.ofNullable(result).orElse(0L);
    }
}
