package com.ichwan.shopper.config;

import com.ichwan.shopper.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifSubscriber implements StreamListener<String, MapRecord<String, String, String>> {

    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        String retryKey = "email:retry"+record.getId();

        try {
            Map<String, String> payload = record.getValue();

            String action = payload.get("action");
            Long productId = payload.get("productId") != null ? Long.valueOf(payload.get("productId")) : null;
            String email = payload.get("email");
            String content = payload.get("message");

            if (email == null || content == null) {
                log.warn("invalid email payload {}",payload);
                ack(record);
                return;
            }

            String subject = mapSubject(action, productId);

            emailService.send(email, subject, content);
            ack(record);
            log.info("email notif sent. subject={}",subject);

        } catch (Exception e) {
            Long retry = redisTemplate.opsForValue().increment(retryKey);
            redisTemplate.expire(retryKey, Duration.ofHours(1));

            if (retry > 3) {
                redisTemplate.opsForStream().add("notif.email.dlq",record.getValue());
                redisTemplate.delete(retryKey);
                ack(record);
                return;
            }

            try {
                long backoff = Math.min(1000L * retry, 10_000L);
                Thread.sleep(backoff);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            log.error("failed to process email notif: {}",e.getMessage());
        }
    }

    private void ack(MapRecord<String, String, String> record) {
        redisTemplate.opsForStream().acknowledge("notif.email.stream","email-group", record.getId());
    }

    private String mapSubject(String action, Long productId) {
        if (action == null) return "Product Notification";

        return switch (action) {
            case "CREATE" -> "Product Created";
            case "UPDATE" -> "Product Updated";
            case "DELETE" -> "Product Deleted";
            default -> "Product Notification";
        } + (productId != null ? " [ID="+productId+"]":"");
    }
}

