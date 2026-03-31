package com.github.dgaponov99.practicum.mybank.accounts.exception;

public class BusinessRuntimeException extends RuntimeException {
    public BusinessRuntimeException(String message) {
        super(message);
    }
}
