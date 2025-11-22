package com.redislabs.edu.redi2read.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash
public class Category {

    @Id
    private String id;
    private String name;
}
