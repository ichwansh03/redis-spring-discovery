package com.ichwan.shopper.service;

import com.ichwan.shopper.config.ProductEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishEmailNotif(ProductEventType action, Long productId, String email, String message) {
        try {
            Map<String, Object> payload = Map.of(
                    "action", action.name(),
                    "productId", productId,
                    "email", email,
                    "message", message
            );

            redisTemplate.convertAndSend("notif.email", objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
