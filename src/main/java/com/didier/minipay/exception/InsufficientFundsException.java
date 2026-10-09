package com.didier.minipay.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId) {
        super("Fondos insuficientes en la cuenta: " + accountId);
    }
}
