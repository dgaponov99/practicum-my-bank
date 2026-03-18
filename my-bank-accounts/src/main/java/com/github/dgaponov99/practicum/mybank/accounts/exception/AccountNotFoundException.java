package com.github.dgaponov99.practicum.mybank.accounts.exception;

public class AccountNotFoundException extends BusinessRuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
