package com.github.dgaponov99.practicum.mybank.accounts.web.controller;

import com.github.dgaponov99.practicum.mybank.accounts.exception.InsufficientFundsException;
import com.github.dgaponov99.practicum.mybank.accounts.service.AccountsService;
import com.github.dgaponov99.practicum.mybank.accounts.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountsController {

    private final AccountsService accountsService;

    @GetMapping
    @PreAuthorize("hasAuthority('accounts.read') && hasRole('SERVICE')")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountsService.getAllAccounts());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('accounts.read') and (!hasRole('USER') or #username == authentication.name)")
    public ResponseEntity<AccountDto> getAccount(@PathVariable String username) {
        return ResponseEntity.ok(accountsService.getAccountDto(username));
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasAuthority('accounts.write') and (!hasRole('USER') or #username == authentication.name)")
    public ResponseEntity<AccountDto> editAccount(@PathVariable String username,
                                                  @RequestBody AccountDataDto accountDataDto) {
        return ResponseEntity.ok(accountsService.editAccount(username, accountDataDto));
    }

    @GetMapping("/{username}/balance")
    @PreAuthorize("hasAuthority('accounts.read') and (!hasRole('USER') or #username == authentication.name)")
    public ResponseEntity<AccountBalanceDto> getAccountBalance(@PathVariable String username) {
        return ResponseEntity.ok(new AccountBalanceDto(accountsService.getAccountBalance(username)));
    }

    @PostMapping("/{username}/credit")
    @PreAuthorize("hasAuthority('accounts.write') && hasRole('SERVICE')")
    public ResponseEntity<AccountDto> creditAccount(@PathVariable String username,
                                                    @RequestBody AmountDto amountDto) {
        return ResponseEntity.ok(accountsService.creditAccount(username, amountDto.amount()));
    }

    @PostMapping("/{username}/debit")
    @PreAuthorize("hasAuthority('accounts.write') && hasRole('SERVICE')")
    public ResponseEntity<AccountDto> debitAccount(@PathVariable String username,
                                                   @RequestBody AmountDto amountDto) throws InsufficientFundsException {
        return ResponseEntity.ok(accountsService.debitAccount(username, amountDto.amount()));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('accounts.write') && hasRole('SERVICE')")
    public ResponseEntity<Void> transfer(@RequestBody TransferDto transferDto) throws InsufficientFundsException {
        accountsService.transfer(transferDto);
        return ResponseEntity.ok().build();
    }

}
