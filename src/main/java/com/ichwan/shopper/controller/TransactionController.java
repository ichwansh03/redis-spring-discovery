package com.ichwan.shopper.controller;

import com.ichwan.shopper.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/multi")
    public ResponseEntity<List<Object>> runMulti(@RequestParam String key, @RequestParam String value) {
        List<Object> result = transactionService.multiExample(key, value);
        return ResponseEntity.ok(result);
    }

}
