package com.github.dgaponov99.practicum.mybank.frontend.client.dto;

import java.time.LocalDate;

public record AccountDto(String username, String name, LocalDate birthDate) {
}
