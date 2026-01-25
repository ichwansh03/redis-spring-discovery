package com.ichwan.shopper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "products")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String email;
    private String role;

}
