package com.didier.minipay.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException (Long id) {
        super("Cuenta no encontrada: " + id);
    }
}
