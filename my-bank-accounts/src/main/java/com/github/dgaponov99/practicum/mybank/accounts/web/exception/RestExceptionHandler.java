package com.github.dgaponov99.practicum.mybank.accounts.web.exception;

import com.github.dgaponov99.practicum.mybank.accounts.exception.AccountNotFoundException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.BusinessRuntimeException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.exception.NotValidException;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.ErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Обработка ошибок валидации {@link ConstraintViolationException}
     * <br><br>
     * Код ответа: {@code 400}
     * <br>
     * Описание ошибки: все сообщения валидации
     */
    @ExceptionHandler(exception = ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(exception.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()));
    }

    /**
     * Обработка ошибок валидации {@link MethodArgumentNotValidException}
     * <br><br>
     * Код ответа: {@code 400}
     * <br>
     * Описание ошибки: все сообщения валидации
     */
    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleException(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(exception.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList()));
    }

    @ExceptionHandler(exception = NotValidException.class)
    public ResponseEntity<ErrorDto> handleException(NotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto(List.of(exception.getMessage())));
    }

    @ExceptionHandler(exception = AccountNotFoundException.class)
    public ResponseEntity<ErrorDto> handleException(BusinessRuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(List.of(exception.getMessage())));
    }

    @ExceptionHandler(exception = InsufficientFundsException.class)
    public ResponseEntity<ErrorDto> handleException(InsufficientFundsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDto(List.of(exception.getMessage())));
    }

}
