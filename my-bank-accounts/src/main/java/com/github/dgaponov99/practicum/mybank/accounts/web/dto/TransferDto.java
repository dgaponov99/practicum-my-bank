package com.github.dgaponov99.practicum.mybank.accounts.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransferDto(
        @NotBlank(message = "Имя пользователя счета списания не может быть пустым")
        String fromUsername,
        @NotBlank(message = "Имя пользователя счета пополнения не может быть пустым")
        String toUsername,
        @Positive(message = "Сумма перевода должна быть положительна")
        int amount) {
}
