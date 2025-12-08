package com.ichwan.shopper.service;

import com.ichwan.shopper.entity.Product;
import com.ichwan.shopper.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private String key(Long productId) {
        return "product:" + productId;
    }

    public Product getProductById(Long id) {
        Product cached = (Product) redisTemplate.opsForValue().get(key(id));
        if (cached != null) {
            log.info("Product {} found in cache", id);
            return cached;
        }

        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            redisTemplate.opsForValue().set(key(id), product);
            log.info("Product {} cached", id);
        }

        return product;
    }

    public Product saveProduct(Product product) {
        Product saved = productRepository.save(product);
        redisTemplate.opsForValue().set(key(saved.getId()), saved);
        log.info("Product {} saved and cached", saved.getId());
        return saved;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        redisTemplate.delete(key(id));
        log.info("Product {} deleted from database and cache", id);
    }
}
