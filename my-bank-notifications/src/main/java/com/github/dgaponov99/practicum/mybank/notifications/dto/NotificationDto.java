package com.github.dgaponov99.practicum.mybank.notifications.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationDto(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username,
        @NotBlank(message = "Текст уведомления не может быть пустым")
        String message) {
}
