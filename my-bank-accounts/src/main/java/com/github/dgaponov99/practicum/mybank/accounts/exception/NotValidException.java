package com.github.dgaponov99.practicum.mybank.accounts.exception;

public class NotValidException extends BusinessRuntimeException {
    public NotValidException(String message) {
        super(message);
    }
}
