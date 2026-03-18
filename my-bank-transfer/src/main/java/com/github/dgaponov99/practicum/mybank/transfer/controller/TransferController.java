package com.github.dgaponov99.practicum.mybank.transfer.controller;

import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferAccountDto;
import com.github.dgaponov99.practicum.mybank.transfer.dto.TransferDto;
import com.github.dgaponov99.practicum.mybank.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/accounts")
    @PreAuthorize("hasAuthority('transfer.read') and (!hasRole('USER') or #username == authentication.name)")
    public ResponseEntity<List<TransferAccountDto>> getTransferAccounts(@RequestParam("username") String username) {
        return ResponseEntity.ok(transferService.getTransferAccounts(username));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('transfer.write') and (!hasRole('USER') or #transferDto.fromUsername() == authentication.name)")
    public ResponseEntity<Void> transfer(@RequestBody TransferDto transferDto) {
        transferService.transfer(transferDto);
        return ResponseEntity.ok().build();
    }

}
