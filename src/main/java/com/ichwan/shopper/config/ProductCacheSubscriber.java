package com.ichwan.shopper.config;

import com.ichwan.shopper.dto.ProductCacheEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheSubscriber implements MessageListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {

        try {
            ProductCacheEvent event = objectMapper.readValue(message.getBody(), ProductCacheEvent.class);
            Cache products = cacheManager.getCache("products");

            if (products == null) return;

            switch (event.getAction()) {
                case "CREATE":
                    products.clear();
                    break;

                case "UPDATE":
                case "DELETE":
                    products.evict(event.getProductId());
                    break;
            }

            log.info("cache processed: {} - {}", event.getAction(), event.getProductId());

        } catch (Exception e) {
            log.error("failed to process cache event",e);
        }
    }
}
