package com.ichwan.shopper.operations.zsets;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/init")
    public ResponseEntity<Void> initTrx() {
        transactionService.initTrx();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-paid")
    public ResponseEntity<Void> markPaid() {
        transactionService.markPaid();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inc-retry")
    public ResponseEntity<Void> incRetry() {
        transactionService.incRetry();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Map<Object, Object>> getTrxState() {
        Map<Object, Object> state = transactionService.getTrxState();
        return ResponseEntity.ok(state);
    }
}

