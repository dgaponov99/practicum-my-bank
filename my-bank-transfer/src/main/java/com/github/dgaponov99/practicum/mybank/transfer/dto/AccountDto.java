package com.github.dgaponov99.practicum.mybank.transfer.dto;

import java.time.LocalDate;

public record AccountDto(String username, String name, LocalDate birthDate) {
}
