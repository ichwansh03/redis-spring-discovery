package com.ichwan.shopper.operations.zsets;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    public static final String KEY = "trx_users";
    private final StringRedisTemplate redisTemplate;

    public void initTrx() {
        redisTemplate.opsForHash().put(KEY, "status", "PENDING");
        redisTemplate.opsForHash().put(KEY, "retry",0);
    }

    public void markPaid() {
        redisTemplate.opsForHash().put(KEY,"status","PAID");
    }

    public void incRetry() {
        redisTemplate.opsForHash().put(KEY, "retry", 1);
    }

    public Map<Object, Object> getTrxState() {
        return redisTemplate.opsForHash().entries(KEY);
    }


}
