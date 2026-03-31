package com.github.dgaponov99.practicum.mybank.transfer.dto;

import java.util.List;

public record ErrorDto(List<String> errors) {
}
