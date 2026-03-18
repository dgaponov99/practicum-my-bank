package com.github.dgaponov99.practicum.mybank.cash.controller;

import com.github.dgaponov99.practicum.mybank.cash.dto.CashAmountDto;
import com.github.dgaponov99.practicum.mybank.cash.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('cash.write') and (!hasRole('USER') or #cashAmountDto.username() == authentication.name)")
    public ResponseEntity<Void> withdraw(@RequestBody CashAmountDto cashAmountDto) {
        cashService.withdraw(cashAmountDto.username(), cashAmountDto.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('cash.write') and (!hasRole('USER') or #cashAmountDto.username() == authentication.name)")
    public ResponseEntity<Void> deposit(@RequestBody CashAmountDto cashAmountDto) {
        cashService.deposit(cashAmountDto.username(), cashAmountDto.amount());
        return ResponseEntity.ok().build();
    }

}
