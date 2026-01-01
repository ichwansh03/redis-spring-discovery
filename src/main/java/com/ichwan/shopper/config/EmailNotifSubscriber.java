package com.ichwan.shopper.config;

import com.ichwan.shopper.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);

            Map<String, Object> payload = objectMapper.readValue(json, new TypeReference<>(){});

            String action = (String) payload.get("action");
            Long productId = payload.get("productId") != null ? Long.valueOf(payload.get("productId").toString()) : null;
            String email = (String) payload.get("email");
            String content = (String) payload.get("message");

            if (email == null || content == null) {
                log.warn("invalid email payload {}",payload);
                return;
            }

            String subject = mapSubject(action, productId);

            emailService.send(email, subject, content);

            log.info("email notif sent. subject={}",subject);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
