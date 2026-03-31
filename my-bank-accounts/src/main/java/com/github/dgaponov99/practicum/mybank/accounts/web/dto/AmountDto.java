package com.github.dgaponov99.practicum.mybank.accounts.web.dto;

import jakarta.validation.constraints.Positive;

public record AmountDto(
        @Positive(message = "Сумма должна быть положительна")
        int amount) {
}
