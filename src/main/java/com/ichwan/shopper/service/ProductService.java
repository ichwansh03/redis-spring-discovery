package com.ichwan.shopper.service;

import com.ichwan.shopper.dto.ProductDto;
import com.ichwan.shopper.entity.Product;
import com.ichwan.shopper.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;
    private final int cacheTtlSeconds;


    public static final String KEY_PREFIX = "product:";

    public ProductService(ProductRepository productRepository, JedisPool jedisPool, ObjectMapper objectMapper, @Value("${cache.product.ttl-seconds}") int cacheTtlSeconds) {
        this.productRepository = productRepository;
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    private String key(Long id) {
        return KEY_PREFIX + id;
    }

    public ProductDto getProduct(Long id) {
        String key = key(id);

        try(Jedis jedis = jedisPool.getResource()) {
            String cached = jedis.get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, ProductDto.class);
            }

        }

        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDto dto = toDto(product);

        try (Jedis jedis = jedisPool.getResource()) {
            String value = objectMapper.writeValueAsString(dto);
            jedis.setex(key, cacheTtlSeconds, value);
        }

        return dto;
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        Product product = toEntity(dto);
        Product saved = productRepository.save(product);
        ProductDto productDto = toDto(saved);

        try(Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key(saved.getId()), cacheTtlSeconds, objectMapper.writeValueAsString(productDto));
        } catch (Exception e) {
            log.info("Failed to write product to cache", e);
        }

        return productDto;
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());

        Product updated = productRepository.save(existing);
        ProductDto productDto = toDto(updated);

        try(Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key(updated.getId()), cacheTtlSeconds, objectMapper.writeValueAsString(productDto));
        } catch (Exception e) {
            log.info("Failed to write product to cache", e);
        }

        return productDto;
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);

        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(key(id));
        } catch (Exception e) {
            log.info("Failed to delete product from cache", e);
        }
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }
}
