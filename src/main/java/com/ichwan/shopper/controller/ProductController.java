package com.ichwan.shopper.controller;

import com.ichwan.shopper.dto.ProductDto;
import com.ichwan.shopper.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable Long id) {
        ProductDto dto = productService.getProduct(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto productDto) {
        // Implementation for creating a product would go here
        ProductDto product = productService.createProduct(productDto);
        URI location = URI.create(String.format("/api/products/%s", product.getId()));
        return ResponseEntity.created(location).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto productDto) {
        // Implementation for updating a product would go here
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Implementation for deleting a product would go here
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
