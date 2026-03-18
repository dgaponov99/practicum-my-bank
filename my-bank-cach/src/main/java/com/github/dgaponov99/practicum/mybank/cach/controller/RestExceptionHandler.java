package com.github.dgaponov99.practicum.mybank.cach.controller;

import com.github.dgaponov99.practicum.mybank.cach.dto.ErrorDto;
import com.github.dgaponov99.practicum.mybank.cach.exception.ExternalMultipleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(exception = ExternalMultipleException.class)
    public ResponseEntity<ErrorDto> handleException(ExternalMultipleException exception) {
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessages()));
    }

}
