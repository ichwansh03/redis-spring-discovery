package com.ichwan.shopper.service;

import com.ichwan.shopper.config.ProductEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publishEmailNotif(ProductEventType action, Long productId, String email, String message) {
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put("action", action.name());
            fields.put("productId", productId != null ? productId.toString() : null);
            fields.put("email", email);
            fields.put("message", message);

            redisTemplate.opsForStream().add("notif.email.stream", fields);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
