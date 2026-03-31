package com.github.dgaponov99.practicum.mybank.accounts.exception;

public class InsufficientFundsException extends BusinessException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
