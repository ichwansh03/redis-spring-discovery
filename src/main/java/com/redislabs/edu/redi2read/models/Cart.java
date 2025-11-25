package com.redislabs.edu.redi2read.models;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class Cart {

    private String id;
    private String userId;

    @Singular
    private Set<CartItem> cartItems;

    public Integer count() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public Double getTotal() {
        return cartItems.stream().mapToDouble(ci -> ci.getPrice() * ci.getQuantity()).sum();
    }
}
