package com.ichwan.shopper.service;

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

        return productRepository.save(product);
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

        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> list() {
        return productRepository.findAll();
    }
}
