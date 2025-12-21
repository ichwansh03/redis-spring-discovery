package com.ichwan.shopper.service;

import com.ichwan.shopper.dto.ProductCacheEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCachePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String action, Long productId) {
        try {
            ProductCacheEvent event = new ProductCacheEvent(action, productId);
            redisTemplate.convertAndSend("product.cache.event", objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("failed to publish cache event",e);
        }
    }
}
