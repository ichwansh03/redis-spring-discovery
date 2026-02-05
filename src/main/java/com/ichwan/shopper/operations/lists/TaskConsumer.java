package com.ichwan.shopper.operations.lists;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskConsumer {

    private static final String QUEUE_KEY = "task_queue";
    private final StringRedisTemplate redisTemplate;

    public String consumeTask() {
        return redisTemplate.opsForList().rightPop(QUEUE_KEY);
    }
}
