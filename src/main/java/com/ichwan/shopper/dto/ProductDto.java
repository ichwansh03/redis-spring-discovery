package com.ichwan.shopper.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}

