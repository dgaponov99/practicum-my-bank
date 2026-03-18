package com.github.dgaponov99.practicum.mybank.cach.controller;

import com.github.dgaponov99.practicum.mybank.cach.dto.CashAmountDto;
import com.github.dgaponov99.practicum.mybank.cach.service.CachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CachController {

    private final CachService cachService;

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('cach.write') and (!hasRole('USER') or #cashAmountDto.username() == authentication.name)")
    public ResponseEntity<Void> withdraw(@RequestBody CashAmountDto cashAmountDto) {
        cachService.withdraw(cashAmountDto.username(), cashAmountDto.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('cach.write') and (!hasRole('USER') or #cashAmountDto.username() == authentication.name)")
    public ResponseEntity<Void> deposit(@RequestBody CashAmountDto cashAmountDto) {
        cachService.debit(cashAmountDto.username(), cashAmountDto.amount());
        return ResponseEntity.ok().build();
    }

}
