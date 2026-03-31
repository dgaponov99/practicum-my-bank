package com.github.dgaponov99.practicum.mybank.accounts.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AccountDataDto(
        @NotBlank(message = "Поле \"Имя\" не может быть пустым")
        @Size(max = 150, message = "Поле \"Имя\" не может быть длиннее {max} символов")
        String name,
        @NotNull(message = "Поле \"Дата рождения\" не может быть пустым")
        @PastOrPresent(message = "Поле \"Дата рождения\" не может быть в будущем")
        LocalDate birthDate) {
}
