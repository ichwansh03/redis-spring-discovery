package com.ichwan.shopper.service;

import com.ichwan.shopper.config.ProductEventType;
import com.ichwan.shopper.dto.ProductDto;
import com.ichwan.shopper.entity.Product;
import com.ichwan.shopper.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationPublisher notificationPublisher;
    private final ProductCachePublisher cachePublisher;

    @Cacheable(value = "products", key = "#id")
    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product create(ProductDto dto) {
        Product product = new Product();
        product.setDescription(dto.getDescription());
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setStock(dto.getStock());
        product.setPrice(dto.getPrice());

        Product saved = productRepository.save(product);
        cachePublisher.publish(ProductEventType.CREATE.name(), saved.getId());
        notificationPublisher.publishEmailNotif(ProductEventType.CREATE, saved.getId(), "ichwansholihin70@gmail.com", "product "+saved.getName()+" has been created");
        return saved;
    }

    @CachePut(value = "products", key = "#id")
    public Product update(Long id, ProductDto newData) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setId(id);
        product.setName(newData.getName());
        product.setSku(newData.getSku());
        product.setDescription(newData.getDescription());
        product.setPrice(newData.getPrice());
        product.setStock(newData.getStock());

        Product updated = productRepository.save(product);
        cachePublisher.publish("UPDATE", id);
        notificationPublisher.publishEmailNotif(ProductEventType.UPDATE, id, "ichwansholihin70@gmail.com", "product "+updated.getName()+" has been updated");
        return updated;
    }

    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
        cachePublisher.publish("DELETE",id);
        notificationPublisher.publishEmailNotif(ProductEventType.DELETE, id, "ichwansholihin70@gmail.com", "product has been deleted");
    }

    public List<Product> list() {
        return productRepository.findAll();
    }
}
