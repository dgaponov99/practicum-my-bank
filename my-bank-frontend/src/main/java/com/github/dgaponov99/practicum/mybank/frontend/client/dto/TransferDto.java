package com.github.dgaponov99.practicum.mybank.frontend.client.dto;

public record TransferDto(String fromUsername, String toUsername, int amount) {
}
