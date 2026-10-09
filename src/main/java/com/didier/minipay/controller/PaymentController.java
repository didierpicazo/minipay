package com.didier.minipay.controller;

import com.didier.minipay.dto.CreatePaymentRequest;
import com.didier.minipay.dto.PaymentResponse;
import com.didier.minipay.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")


public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePaymentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(idempotencyKey, request));
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id){
        return paymentService.findById(id);
    }

}
