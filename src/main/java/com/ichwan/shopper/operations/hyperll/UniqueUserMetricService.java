package com.ichwan.shopper.operations.hyperll;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniqueUserMetricService {

    public static final String HLL_KEY = "metric:daily:users";
    private final StringRedisTemplate redisTemplate;

    public void recordUser(String userId) {
        redisTemplate.opsForHyperLogLog().add(HLL_KEY, userId);
    }

    public long getEstimatedUniqueUsers() {
        return redisTemplate.opsForHyperLogLog().size(HLL_KEY);
    }
}
