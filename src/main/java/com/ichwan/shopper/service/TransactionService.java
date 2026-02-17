package com.ichwan.shopper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final StringRedisTemplate redisTemplate;

    public List<Object> multiExample(String key, String value) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();  // Start transaction

                operations.opsForValue().set(key, value);
                operations.opsForValue().increment("multiCounter");

                return operations.exec();  // Execute transaction
            }
        });

    }

}
