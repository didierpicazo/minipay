package com.didier.minipay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
public record CreatePaymentRequest(
        @NotNull Long  fromAccountId,
        @NotNull Long toAccountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {}
