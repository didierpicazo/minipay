package com.didier.minipay.dto;

import com.didier.minipay.model.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        PaymentStatus status
) {}

