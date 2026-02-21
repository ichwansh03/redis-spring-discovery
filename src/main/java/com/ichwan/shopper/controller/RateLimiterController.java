package com.ichwan.shopper.controller;

import com.ichwan.shopper.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
@RequiredArgsConstructor
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    @PostMapping("/hit")
    public ResponseEntity<Long> hit(@RequestParam String key,
                                    @RequestParam(defaultValue = "60") long expireSeconds) {
        Long count = rateLimiterService.hit(key, expireSeconds);
        return ResponseEntity.ok(count);
    }

}

